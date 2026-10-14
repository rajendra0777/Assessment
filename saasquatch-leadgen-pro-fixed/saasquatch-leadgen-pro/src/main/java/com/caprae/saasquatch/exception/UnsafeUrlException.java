package com.caprae.saasquatch.exception;

/** Thrown when a user-supplied URL is rejected by the SSRF guard. Mapped to HTTP 400. */
public class UnsafeUrlException extends IllegalArgumentException {
    public UnsafeUrlException(String message) {
        super(message);
    }
}
