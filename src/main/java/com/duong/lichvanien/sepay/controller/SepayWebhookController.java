package com.duong.lichvanien.sepay.controller;

import com.duong.lichvanien.sepay.config.SepayProperties;
import com.duong.lichvanien.sepay.service.SepayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * REST Controller for SePay webhook.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/sepay")
@RequiredArgsConstructor
public class SepayWebhookController {

    private final SepayService sepayService;
    private final SepayProperties sepayProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            @RequestBody(required = false) Map<String, Object> rawRequest) {
        
        try {
            log.info("=== SePay Webhook Received ===");
            
            if (rawRequest == null) {
                log.error("Request body is null");
                return ResponseEntity.status(400).body("Request body is required");
            }
            
            // Log raw request để debug
            log.info("Raw request: {}", rawRequest);
            
            // Log all fields
            rawRequest.forEach((key, value) -> {
                log.info("Field: {} = {} (type: {})", key, value, 
                        value != null ? value.getClass().getName() : "null");
            });
            
            // Convert to DTO - handle SePay actual format
            SepayService.SepayWebhookRequest request = new SepayService.SepayWebhookRequest();
            
            // Map transactionId: SePay uses "referenceCode" or "id"
            Object transactionIdObj = rawRequest.getOrDefault("referenceCode", 
                    rawRequest.getOrDefault("id", 
                    rawRequest.getOrDefault("transactionId", 
                    rawRequest.get("transaction_id"))));
            if (transactionIdObj != null) {
                request.setTransactionId(transactionIdObj.toString());
            }
            
            // Extract content from SePay format
            // SePay content format: "113952205002-LVNXUFC69D3A1-CHUYEN TIEN-..."
            // We need to extract "LVNXUFC69D3A1" (content prefix + UUID)
            Object contentObj = rawRequest.get("content");
            if (contentObj != null) {
                String contentStr = contentObj.toString();
                log.info("Original content from SePay: {}", contentStr);
                
                // Extract content: look for pattern "LVNXU" + 8 alphanumeric chars
                String extractedContent = extractContentFromSepayFormat(contentStr);
                if (extractedContent != null && !extractedContent.isBlank()) {
                    request.setContent(extractedContent);
                    log.info("Extracted content: {} -> {}", contentStr, extractedContent);
                } else {
                    // Fallback: use full content if extraction fails
                    request.setContent(contentStr);
                    log.warn("Could not extract content, using full content: {}", contentStr);
                }
            }
            
            // Handle amount: SePay uses "transferAmount"
            Object amountObj = rawRequest.getOrDefault("transferAmount",
                    rawRequest.getOrDefault("amount", 
                    rawRequest.get("amount_vnd")));
            if (amountObj != null) {
                if (amountObj instanceof Number) {
                    request.setAmount(new BigDecimal(amountObj.toString()));
                } else if (amountObj instanceof String) {
                    try {
                        request.setAmount(new BigDecimal((String) amountObj));
                    } catch (NumberFormatException e) {
                        log.error("Invalid amount format: {}", amountObj);
                        return ResponseEntity.status(400)
                                .body("Invalid amount format: " + amountObj);
                    }
                }
            }
            
            // Store full raw request as rawResponse
            try {
                request.setRawResponse(objectMapper.writeValueAsString(rawRequest));
            } catch (Exception e) {
                log.warn("Failed to serialize raw request: {}", e.getMessage());
                request.setRawResponse(rawRequest.toString());
            }
            
            log.info("Mapped request: transactionId={}, content={}, amount={}", 
                    request.getTransactionId(), request.getContent(), request.getAmount());
            
            sepayService.processWebhook(request);
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            log.error("=== SePay Webhook Error ===");
            log.error("Error type: {}", e.getClass().getName());
            log.error("Error message: {}", e.getMessage());
            log.error("Stack trace:", e);
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error: " + e.getMessage() + ". Check logs for details.");
        }
    }

    @GetMapping("/check/{transactionId}")
    public ResponseEntity<?> checkTransaction(@PathVariable Long transactionId) {
        return sepayService.getTransaction(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Extract content from SePay content format.
     * SePay format: "113952205002-LVNXUFC69D3A1-CHUYEN TIEN-..."
     * We need to extract "LVNXUFC69D3A1" (content prefix + UUID)
     * 
     * @param content Original content string from SePay
     * @return Extracted content (e.g., "LVNXUFC69D3A1") or null if not found
     */
    private String extractContentFromSepayFormat(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }
        
        String contentPrefix = sepayProperties.getContentPrefix(); // "LVNXU"
        if (contentPrefix == null || contentPrefix.isBlank()) {
            contentPrefix = "LVNXU"; // Default fallback
        }
        
        // Method 1: Regex to find content prefix followed by 8 alphanumeric characters
        // Pattern: "LVNXU" + 8 alphanumeric = 13 characters total
        String regexPattern = contentPrefix + "[A-Z0-9]{8}";
        Pattern pattern = Pattern.compile(regexPattern);
        java.util.regex.Matcher matcher = pattern.matcher(content);
        
        if (matcher.find()) {
            String extracted = matcher.group(0);
            log.info("Extracted content using regex: {} -> {}", content, extracted);
            return extracted;
        }
        
        // Method 2: Split by "-" and find part containing content prefix
        String[] parts = content.split("-");
        for (String part : parts) {
            if (part.startsWith(contentPrefix) && part.length() == contentPrefix.length() + 8) {
                log.info("Extracted content from split: {} -> {}", content, part);
                return part;
            }
        }
        
        // Method 3: Try to find content prefix anywhere in the string
        int prefixIndex = content.indexOf(contentPrefix);
        if (prefixIndex >= 0 && prefixIndex + 13 <= content.length()) {
            String extracted = content.substring(prefixIndex, prefixIndex + 13);
            // Verify it matches the pattern
            if (extracted.matches(regexPattern)) {
                log.info("Extracted content using index: {} -> {}", content, extracted);
                return extracted;
            }
        }
        
        log.warn("Could not extract content from SePay format: {}", content);
        return null;
    }
}

