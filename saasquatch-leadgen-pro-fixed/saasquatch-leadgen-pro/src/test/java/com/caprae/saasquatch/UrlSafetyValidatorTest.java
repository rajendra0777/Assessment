package com.caprae.saasquatch;

import com.caprae.saasquatch.exception.UnsafeUrlException;
import com.caprae.saasquatch.service.UrlSafetyValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Uses IP-literal URLs only, so no DNS lookups are needed. */
class UrlSafetyValidatorTest {

    @Test
    @DisplayName("Blocks loopback, private, link-local (cloud metadata) and IPv6 internal addresses")
    void blocksInternalAddresses() {
        for (String url : new String[]{
                "http://127.0.0.1/", "http://10.0.0.5/", "http://192.168.1.10/", "http://172.16.0.1/",
                "http://169.254.169.254/latest/meta-data/", "http://100.64.0.1/", "http://0.0.0.0/",
                "http://[::1]/", "http://[fd00::1]/"}) {
            assertThrows(UnsafeUrlException.class, () -> UrlSafetyValidator.validate(url), url);
        }
    }

    @Test
    @DisplayName("Blocks non-http schemes, odd ports and embedded credentials")
    void blocksBadShapes() {
        assertThrows(UnsafeUrlException.class, () -> UrlSafetyValidator.validate("ftp://8.8.8.8/"));
        assertThrows(UnsafeUrlException.class, () -> UrlSafetyValidator.validate("file:///etc/passwd"));
        assertThrows(UnsafeUrlException.class, () -> UrlSafetyValidator.validate("http://8.8.8.8:8080/"));
        assertThrows(UnsafeUrlException.class, () -> UrlSafetyValidator.validate("http://user:pass@8.8.8.8/"));
    }

    @Test
    @DisplayName("Allows a public address on a standard port")
    void allowsPublicAddress() {
        assertDoesNotThrow(() -> UrlSafetyValidator.validate("https://8.8.8.8/"));
        assertDoesNotThrow(() -> UrlSafetyValidator.validate("http://8.8.8.8:80/path"));
    }
}
