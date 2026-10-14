package com.caprae.saasquatch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Minimal robots.txt evaluator (RFC 9309 semantics): picks the most specific matching
 * user-agent group (falling back to "*"), then the longest matching rule wins; Allow wins ties.
 */
public final class RobotsTxtPolicy {

    private RobotsTxtPolicy() {}

    private record Rule(boolean allow, String pattern) {}

    private static final class Group {
        final List<String> agents = new ArrayList<>();
        final List<Rule> rules = new ArrayList<>();
    }

    public static boolean isAllowed(String robotsTxt, String userAgentToken, String path) {
        if (robotsTxt == null || robotsTxt.isBlank()) return true;
        String agent = userAgentToken.toLowerCase(Locale.ROOT);
        String target = (path == null || path.isEmpty()) ? "/" : path;

        List<Group> groups = parse(robotsTxt);
        Group chosen = null;
        int bestLen = -1;
        Group star = null;
        for (Group g : groups) {
            for (String a : g.agents) {
                if (a.equals("*")) {
                    star = g;
                } else if (agent.contains(a) && a.length() > bestLen) {
                    chosen = g;
                    bestLen = a.length();
                }
            }
        }
        if (chosen == null) chosen = star;
        if (chosen == null) return true;

        boolean allowed = true;
        int bestMatch = -1;
        for (Rule r : chosen.rules) {
            if (r.pattern().isEmpty()) continue; // "Disallow:" with no value allows everything
            if (matches(r.pattern(), target)) {
                int len = r.pattern().length();
                if (len > bestMatch || (len == bestMatch && r.allow())) {
                    bestMatch = len;
                    allowed = r.allow();
                }
            }
        }
        return allowed;
    }

    private static List<Group> parse(String body) {
        List<Group> groups = new ArrayList<>();
        Group current = null;
        boolean lastWasAgent = false;
        for (String raw : body.split("\\R")) {
            String line = raw;
            int hash = line.indexOf('#');
            if (hash >= 0) line = line.substring(0, hash);
            line = line.trim();
            int colon = line.indexOf(':');
            if (colon < 0) continue;
            String key = line.substring(0, colon).trim().toLowerCase(Locale.ROOT);
            String value = line.substring(colon + 1).trim();
            switch (key) {
                case "user-agent" -> {
                    if (current == null || !lastWasAgent) {
                        current = new Group();
                        groups.add(current);
                    }
                    current.agents.add(value.toLowerCase(Locale.ROOT));
                    lastWasAgent = true;
                }
                case "allow", "disallow" -> {
                    if (current != null) current.rules.add(new Rule(key.equals("allow"), value));
                    lastWasAgent = false;
                }
                default -> lastWasAgent = false;
            }
        }
        return groups;
    }

    private static boolean matches(String pattern, String path) {
        boolean anchored = pattern.endsWith("$");
        String p = anchored ? pattern.substring(0, pattern.length() - 1) : pattern;
        String[] parts = p.split("\\*", -1);
        int pos = 0;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == 0) {
                if (!path.startsWith(part)) return false;
                pos = part.length();
            } else {
                int idx = path.indexOf(part, pos);
                if (idx < 0) return false;
                pos = idx + part.length();
            }
        }
        return !anchored || pos == path.length() || p.endsWith("*");
    }
}
