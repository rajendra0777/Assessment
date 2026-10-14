package com.caprae.saasquatch.exception;

/** Thrown when a target site disallows automated access (robots.txt). Mapped to HTTP 422. */
public class ScrapeBlockedException extends RuntimeException {
    public ScrapeBlockedException(String message) {
        super(message);
    }
}
