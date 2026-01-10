package com.duong.lichvanien.user.service;

import com.duong.lichvanien.user.dto.FingerprintRequest;
import com.duong.lichvanien.user.dto.FingerprintResponse;
import com.duong.lichvanien.user.entity.FingerprintEntity;
import com.duong.lichvanien.user.repository.FingerprintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FingerprintServiceTest {

    @Mock
    private FingerprintRepository fingerprintRepository;

    @InjectMocks
    private FingerprintService fingerprintService;

    private MockHttpServletRequest request;
    private FingerprintRequest clientData;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.1");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0");
        request.addHeader("Accept-Language", "vi-VN,vi;q=0.9,en;q=0.8");
        request.addHeader("Accept-Encoding", "gzip, deflate, br");

        clientData = FingerprintRequest.builder()
                .canvasHash("abc123")
                .webglHash("def456")
                .screenSize("1920x1080")
                .timezone("Asia/Ho_Chi_Minh")
                .language("vi-VN")
                .platform("web")
                .deviceType("desktop")
                .build();
    }

    @Test
    void generateFingerprint_NewFingerprint_ShouldCreateAndReturn() {
        // Given
        when(fingerprintRepository.findByFingerprintId(any())).thenReturn(Optional.empty());
        when(fingerprintRepository.save(any())).thenAnswer(invocation -> {
            FingerprintEntity entity = invocation.getArgument(0);
            entity.setFirstSeenAt(LocalDateTime.now());
            return entity;
        });

        // When
        FingerprintResponse response = fingerprintService.generateFingerprint(clientData, request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getFingerprintId());
        assertTrue(response.isNew());
        assertEquals(1, response.getUsageCount());
        verify(fingerprintRepository).save(any());
    }

    @Test
    void generateFingerprint_ExistingFingerprint_ShouldIncrementUsage() {
        // Given
        FingerprintEntity existing = FingerprintEntity.builder()
                .fingerprintId("existing-id")
                .usageCount(5)
                .firstSeenAt(LocalDateTime.now().minusDays(10))
                .build();

        when(fingerprintRepository.findByFingerprintId(any())).thenReturn(Optional.of(existing));
        when(fingerprintRepository.save(any())).thenReturn(existing);

        // When
        FingerprintResponse response = fingerprintService.generateFingerprint(clientData, request);

        // Then
        assertNotNull(response);
        assertFalse(response.isNew());
        assertEquals(6, response.getUsageCount());
        verify(fingerprintRepository).save(existing);
    }

    @Test
    void generateFingerprintId_ShouldBeConsistent() {
        // Given - same request and client data

        // When
        String id1 = fingerprintService.generateFingerprintId(clientData, request);
        String id2 = fingerprintService.generateFingerprintId(clientData, request);

        // Then
        assertEquals(id1, id2, "Same input should produce same fingerprint ID");
    }

    @Test
    void generateFingerprintId_DifferentInput_ShouldBeDifferent() {
        // Given
        FingerprintRequest differentClient = FingerprintRequest.builder()
                .canvasHash("xyz789")
                .webglHash("uvw123")
                .screenSize("1366x768")
                .build();

        // When
        String id1 = fingerprintService.generateFingerprintId(clientData, request);
        String id2 = fingerprintService.generateFingerprintId(differentClient, request);

        // Then
        assertNotEquals(id1, id2, "Different input should produce different fingerprint ID");
    }

    @Test
    void hashFingerprint_ShouldReturnSHA256() {
        // When
        String hash = fingerprintService.hashFingerprint("test data");

        // Then
        assertNotNull(hash);
        assertEquals(64, hash.length(), "SHA-256 hash should be 64 hex characters");
    }

    @Test
    void getClientIpAddress_WithXForwardedFor_ShouldReturnFirstIp() {
        // Given
        request.addHeader("X-Forwarded-For", "203.0.113.1, 198.51.100.2, 192.0.2.3");

        // When
        String ip = fingerprintService.getClientIpAddress(request);

        // Then
        assertEquals("203.0.113.1", ip);
    }

    @Test
    void getClientIpAddress_WithoutProxy_ShouldReturnRemoteAddr() {
        // When
        String ip = fingerprintService.getClientIpAddress(request);

        // Then
        assertEquals("192.168.1.1", ip);
    }

    @Test
    void normalizeFingerprint_WithNullClientData_ShouldStillWork() {
        // When
        String normalized = fingerprintService.normalizeFingerprint(null, request);

        // Then
        assertNotNull(normalized);
        assertTrue(normalized.contains("ip:192.168.1.1"));
    }
}

