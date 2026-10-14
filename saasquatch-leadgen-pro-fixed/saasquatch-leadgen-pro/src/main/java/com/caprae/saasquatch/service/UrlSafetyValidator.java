package com.caprae.saasquatch.service;

import com.caprae.saasquatch.exception.UnsafeUrlException;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * SSRF guard for the scraper. Only public http(s) hosts on standard ports are allowed.
 * Loopback, private, link-local (incl. cloud metadata 169.254.169.254), CGNAT, multicast
 * and IPv6 unique-local addresses are rejected. Call this for the initial URL and for every redirect hop.
 *
 * Known limitation: DNS is resolved here and again by the HTTP client (DNS-rebinding window).
 * In production, pin the resolved IP or fetch through an egress proxy that enforces the same rules.
 */
public final class UrlSafetyValidator {

    private UrlSafetyValidator() {}

    public static URI validate(String url) {
        final URI uri;
        try {
            uri = URI.create(url.trim());
        } catch (IllegalArgumentException e) {
            throw new UnsafeUrlException("Malformed URL");
        }
        String scheme = uri.getScheme();
        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new UnsafeUrlException("Only http and https URLs are allowed");
        }
        if (uri.getUserInfo() != null) {
            throw new UnsafeUrlException("URLs with embedded credentials are not allowed");
        }
        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new UnsafeUrlException("URL has no host");
        }
        int port = uri.getPort();
        if (port != -1 && port != 80 && port != 443) {
            throw new UnsafeUrlException("Only ports 80 and 443 are allowed");
        }
        final InetAddress[] addresses;
        try {
            addresses = InetAddress.getAllByName(host);
        } catch (UnknownHostException e) {
            throw new UnsafeUrlException("Host could not be resolved");
        }
        for (InetAddress address : addresses) {
            if (isUnsafe(address)) {
                throw new UnsafeUrlException("Target resolves to a non-public address and was blocked");
            }
        }
        return uri;
    }

    static boolean isUnsafe(InetAddress a) {
        if (a.isAnyLocalAddress() || a.isLoopbackAddress() || a.isLinkLocalAddress()
                || a.isSiteLocalAddress() || a.isMulticastAddress()) {
            return true;
        }
        byte[] b = a.getAddress();
        if (b.length == 4) {
            int first = b[0] & 0xFF;
            int second = b[1] & 0xFF;
            if (first == 0) return true;                               // 0.0.0.0/8
            if (first == 100 && second >= 64 && second <= 127) return true; // 100.64.0.0/10 CGNAT
            if (first == 198 && (second == 18 || second == 19)) return true; // benchmarking range
            return false;
        }
        return (b[0] & 0xFE) == 0xFC;                                  // fc00::/7 unique-local
    }
}
