package edu.demo.scfs.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TrackingCodeServiceTest {
    @Test
    void createsOneTimeTrackingCodesAndVerifiesOnlyMatchingPlainText() {
        TrackingCodeService service = new TrackingCodeService();

        String plain = service.generatePlainCode();
        String hash = service.hash(plain);

        assertThat(plain).matches("[A-Z0-9]{10}");
        assertThat(hash).isNotEqualTo(plain);
        assertThat(service.matches(plain, hash)).isTrue();
        assertThat(service.matches("WRONGCODE1", hash)).isFalse();
    }
}

