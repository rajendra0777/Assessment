package com.caprae.saasquatch;

import com.caprae.saasquatch.entity.Lead;
import com.caprae.saasquatch.service.DeduplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeduplicationServiceTest {

    private static Lead lead(String name, String domain) {
        Lead l = new Lead();
        l.setCompanyName(name);
        l.setDomain(domain);
        return l;
    }

    @Test
    @DisplayName("Domains are normalized: scheme, www, path and case are ignored")
    void normalizesDomain() {
        assertEquals("acme.com", DeduplicationService.normalizeDomain("https://WWW.Acme.com/about"));
        assertEquals("acme.com", DeduplicationService.normalizeDomain(" acme.com "));
    }

    @Test
    @DisplayName("Company names are normalized: punctuation and legal suffixes are ignored")
    void normalizesName() {
        assertEquals("northgate precision machining", DeduplicationService.normalizeName("Northgate Precision Machining, Inc."));
        assertEquals("acme", DeduplicationService.normalizeName("ACME LLC"));
    }

    @Test
    @DisplayName("Same domain is an exact duplicate even when the names differ")
    void detectsSameDomain() {
        var m = DeduplicationService.findDuplicate("Totally Different Name", "www.acme.com", List.of(lead("Acme", "acme.com")));
        assertTrue(m.isPresent());
        assertEquals(1.0, m.get().similarity());
        assertEquals("same domain", m.get().reason());
    }

    @Test
    @DisplayName("Name variants on different domains are flagged as possible duplicates")
    void detectsFuzzyName() {
        var existing = List.of(lead("Northgate Precision Machining", "northgate-machining.com"));
        var m = DeduplicationService.findDuplicate("Northgate Precision Machining Inc.", "northgate-pm.com", existing);
        assertTrue(m.isPresent());
        assertTrue(m.get().similarity() >= DeduplicationService.NAME_SIMILARITY_THRESHOLD);
    }

    @Test
    @DisplayName("Unrelated companies are not flagged")
    void ignoresDifferentCompanies() {
        var existing = List.of(lead("Harborline HVAC Services", "harborline.com"));
        assertTrue(DeduplicationService.findDuplicate("Brightwell Dental Partners", "brightwell.com", existing).isEmpty());
    }

    @Test
    @DisplayName("Jaro-Winkler is symmetric-ish, bounded, and 1.0 for identical strings")
    void jaroWinklerBounds() {
        assertEquals(1.0, DeduplicationService.jaroWinkler("acme", "acme"));
        assertEquals(0.0, DeduplicationService.jaroWinkler("abc", "xyz"));
        double s = DeduplicationService.jaroWinkler("martha", "marhta");
        assertTrue(s > 0.95 && s <= 1.0);
    }
}
