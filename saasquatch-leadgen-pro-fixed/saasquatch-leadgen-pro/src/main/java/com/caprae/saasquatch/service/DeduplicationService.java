package com.caprae.saasquatch.service;

import com.caprae.saasquatch.entity.Lead;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Duplicate detection for leads: exact match on normalized domain, then fuzzy match on
 * normalized company name using Jaro-Winkler similarity. Pure functions, no Spring wiring,
 * so they are trivial to unit test.
 */
public final class DeduplicationService {

    public static final double NAME_SIMILARITY_THRESHOLD = 0.93;
    private static final Set<String> LEGAL_SUFFIXES = Set.of(
            "inc", "incorporated", "llc", "ltd", "limited", "corp", "corporation", "co", "company", "gmbh", "plc", "lp", "llp");

    public record Match(Lead existing, double similarity, String reason) {}

    private DeduplicationService() {}

    public static String normalizeDomain(String domain) {
        if (domain == null) return "";
        String d = domain.trim().toLowerCase(Locale.ROOT)
                .replaceFirst("^https?://", "")
                .replaceFirst("^www\\.", "");
        int slash = d.indexOf('/');
        return slash >= 0 ? d.substring(0, slash) : d;
    }

    public static String normalizeName(String name) {
        if (name == null) return "";
        String[] tokens = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]", " ").trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String t : tokens) {
            if (t.isEmpty() || LEGAL_SUFFIXES.contains(t)) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(t);
        }
        return sb.toString();
    }

    public static Optional<Match> findDuplicate(String companyName, String domain, List<Lead> existingLeads) {
        String nd = normalizeDomain(domain);
        String nn = normalizeName(companyName);
        Match best = null;
        for (Lead l : existingLeads) {
            if (!nd.isEmpty() && nd.equals(normalizeDomain(l.getDomain()))) {
                return Optional.of(new Match(l, 1.0, "same domain"));
            }
            if (nn.isEmpty()) continue;
            double sim = jaroWinkler(nn, normalizeName(l.getCompanyName()));
            if (sim >= NAME_SIMILARITY_THRESHOLD && (best == null || sim > best.similarity())) {
                best = new Match(l, sim, "similar company name");
            }
        }
        return Optional.ofNullable(best);
    }

    public static double jaroWinkler(String a, String b) {
        if (a.equals(b)) return a.isEmpty() ? 0.0 : 1.0;
        if (a.isEmpty() || b.isEmpty()) return 0.0;
        int window = Math.max(0, Math.max(a.length(), b.length()) / 2 - 1);
        boolean[] aMatched = new boolean[a.length()];
        boolean[] bMatched = new boolean[b.length()];
        int matches = 0;
        for (int i = 0; i < a.length(); i++) {
            int start = Math.max(0, i - window);
            int end = Math.min(b.length() - 1, i + window);
            for (int j = start; j <= end; j++) {
                if (!bMatched[j] && a.charAt(i) == b.charAt(j)) {
                    aMatched[i] = true;
                    bMatched[j] = true;
                    matches++;
                    break;
                }
            }
        }
        if (matches == 0) return 0.0;
        int transpositions = 0;
        int k = 0;
        for (int i = 0; i < a.length(); i++) {
            if (!aMatched[i]) continue;
            while (!bMatched[k]) k++;
            if (a.charAt(i) != b.charAt(k)) transpositions++;
            k++;
        }
        double m = matches;
        double jaro = (m / a.length() + m / b.length() + (m - transpositions / 2.0) / m) / 3.0;
        int prefix = 0;
        for (int i = 0; i < Math.min(4, Math.min(a.length(), b.length())); i++) {
            if (a.charAt(i) == b.charAt(i)) prefix++; else break;
        }
        return jaro + prefix * 0.1 * (1 - jaro);
    }
}
