package com.duong.lichvanien.user.service;

import com.duong.lichvanien.user.dto.PaymentCheckResponse;
import com.duong.lichvanien.user.dto.PaymentRequest;
import com.duong.lichvanien.user.dto.PaymentResponse;
import com.duong.lichvanien.user.entity.ContentAccessEntity;
import com.duong.lichvanien.user.entity.PaymentTransactionEntity;
import com.duong.lichvanien.user.enums.PaymentStatus;
import com.duong.lichvanien.user.enums.TransactionType;
import com.duong.lichvanien.user.exception.PaymentRequiredException;
import com.duong.lichvanien.user.repository.ContentAccessRepository;
import com.duong.lichvanien.user.repository.PaymentTransactionRepository;
import com.duong.lichvanien.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentTransactionRepository transactionRepository;

    @Mock
    private ContentAccessRepository contentAccessRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FingerprintService fingerprintService;

    @InjectMocks
    private PaymentService paymentService;

    private MockHttpServletRequest httpRequest;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        httpRequest = new MockHttpServletRequest();
        httpRequest.setRemoteAddr("192.168.1.1");

        paymentRequest = PaymentRequest.builder()
                .transactionType(TransactionType.TUVI_INTERPRETATION)
                .amount(new BigDecimal("50000"))
                .currency("VND")
                .chartHash("test-chart-hash")
                .build();
    }

    @Test
    void createTransaction_NewTransaction_ShouldCreateAndReturn() {
        // Given
        String fingerprintId = "test-fingerprint";
        when(fingerprintService.getClientIpAddress(any())).thenReturn("192.168.1.1");
        when(contentAccessRepository.hasActiveAccess(any(), any(), any(), any())).thenReturn(false);
        when(transactionRepository.save(any())).thenAnswer(invocation -> {
            PaymentTransactionEntity entity = invocation.getArgument(0);
            entity.setCreatedAt(LocalDateTime.now());
            return entity;
        });

        // When
        PaymentResponse response = paymentService.createTransaction(
                paymentRequest, fingerprintId, null, null, httpRequest);

        // Then
        assertNotNull(response);
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());
        verify(transactionRepository).save(any());
    }

    @Test
    void createTransaction_AlreadyPaid_ShouldReturnCompleted() {
        // Given
        String fingerprintId = "test-fingerprint";
        when(contentAccessRepository.hasActiveAccess(eq(fingerprintId), any(), any(), any())).thenReturn(true);

        // When
        PaymentResponse response = paymentService.createTransaction(
                paymentRequest, fingerprintId, null, null, httpRequest);

        // Then
        assertNotNull(response);
        assertEquals(PaymentStatus.COMPLETED, response.getPaymentStatus());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void hasActiveAccess_WithAccess_ShouldReturnTrue() {
        // Given
        String fingerprintId = "test-fingerprint";
        String contentType = "TUVI_INTERPRETATION";
        String contentId = "test-chart";
        when(contentAccessRepository.hasActiveAccess(eq(fingerprintId), eq(contentType), eq(contentId), any()))
                .thenReturn(true);

        // When
        boolean result = paymentService.hasActiveAccess(fingerprintId, contentType, contentId);

        // Then
        assertTrue(result);
    }

    @Test
    void hasActiveAccess_WithoutAccess_ShouldReturnFalse() {
        // Given
        String fingerprintId = "test-fingerprint";
        String contentType = "TUVI_INTERPRETATION";
        String contentId = "test-chart";
        when(contentAccessRepository.hasActiveAccess(eq(fingerprintId), eq(contentType), eq(contentId), any()))
                .thenReturn(false);

        // When
        boolean result = paymentService.hasActiveAccess(fingerprintId, contentType, contentId);

        // Then
        assertFalse(result);
    }

    @Test
    void verifyPaymentOrThrow_WithAccess_ShouldNotThrow() {
        // Given
        String fingerprintId = "test-fingerprint";
        String contentType = "TUVI_INTERPRETATION";
        String contentId = "test-chart";
        when(contentAccessRepository.hasActiveAccess(eq(fingerprintId), eq(contentType), eq(contentId), any()))
                .thenReturn(true);

        // When/Then
        assertDoesNotThrow(() -> 
                paymentService.verifyPaymentOrThrow(fingerprintId, contentType, contentId));
        verify(contentAccessRepository).incrementAccessCount(eq(fingerprintId), eq(contentType), eq(contentId), any());
    }

    @Test
    void verifyPaymentOrThrow_WithoutAccess_ShouldThrow() {
        // Given
        String fingerprintId = "test-fingerprint";
        String contentType = "TUVI_INTERPRETATION";
        String contentId = "test-chart";
        when(contentAccessRepository.hasActiveAccess(eq(fingerprintId), eq(contentType), eq(contentId), any()))
                .thenReturn(false);

        // When/Then
        assertThrows(PaymentRequiredException.class, () -> 
                paymentService.verifyPaymentOrThrow(fingerprintId, contentType, contentId));
    }

    @Test
    void checkPaymentEligibility_WithAccess_ShouldReturnPaid() {
        // Given
        String fingerprintId = "test-fingerprint";
        String contentType = "TUVI_INTERPRETATION";
        String contentId = "test-chart";
        
        ContentAccessEntity access = ContentAccessEntity.builder()
                .fingerprintId(fingerprintId)
                .contentType(contentType)
                .contentId(contentId)
                .accessGrantedAt(LocalDateTime.now())
                .isActive(true)
                .build();
        
        when(contentAccessRepository.findByFingerprintIdAndContentTypeAndContentId(
                eq(fingerprintId), eq(contentType), eq(contentId)))
                .thenReturn(Optional.of(access));

        // When
        PaymentCheckResponse response = paymentService.checkPaymentEligibility(
                fingerprintId, contentType, contentId);

        // Then
        assertTrue(response.isPaid());
        assertTrue(response.isHasAccess());
    }

    @Test
    void checkPaymentEligibility_WithoutAccess_ShouldReturnUnpaid() {
        // Given
        String fingerprintId = "test-fingerprint";
        String contentType = "TUVI_INTERPRETATION";
        String contentId = "test-chart";
        
        when(contentAccessRepository.findByFingerprintIdAndContentTypeAndContentId(
                eq(fingerprintId), eq(contentType), eq(contentId)))
                .thenReturn(Optional.empty());

        // When
        PaymentCheckResponse response = paymentService.checkPaymentEligibility(
                fingerprintId, contentType, contentId);

        // Then
        assertFalse(response.isPaid());
        assertFalse(response.isHasAccess());
    }

    @Test
    void completePayment_ValidTransaction_ShouldComplete() {
        // Given
        String transactionUuid = "test-uuid";
        PaymentTransactionEntity transaction = PaymentTransactionEntity.builder()
                .transactionUuid(transactionUuid)
                .fingerprintId("test-fingerprint")
                .transactionType(TransactionType.TUVI_INTERPRETATION)
                .amount(new BigDecimal("50000"))
                .paymentStatus(PaymentStatus.PENDING)
                .contentType("TUVI_INTERPRETATION")
                .contentId("test-chart")
                .build();
        
        when(transactionRepository.findByTransactionUuid(eq(transactionUuid)))
                .thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any())).thenReturn(transaction);
        when(contentAccessRepository.findByFingerprintIdAndContentTypeAndContentId(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(contentAccessRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When
        PaymentResponse response = paymentService.completePayment(
                transactionUuid, "gateway-123", "{}");

        // Then
        assertEquals(PaymentStatus.COMPLETED, response.getPaymentStatus());
        verify(contentAccessRepository).save(any());
    }
}

