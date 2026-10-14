package com.caprae.saasquatch;

import com.caprae.saasquatch.service.RobotsTxtPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RobotsTxtPolicyTest {

    private static final String BOT = "SaaSquatchLeadGenBot";

    @Test
    @DisplayName("Empty robots.txt allows everything")
    void emptyAllows() {
        assertTrue(RobotsTxtPolicy.isAllowed("", BOT, "/"));
    }

    @Test
    @DisplayName("Wildcard group with Disallow: / blocks the bot")
    void wildcardDisallowAll() {
        assertFalse(RobotsTxtPolicy.isAllowed("User-agent: *\nDisallow: /", BOT, "/"));
    }

    @Test
    @DisplayName("Empty Disallow value allows everything")
    void emptyDisallowAllows() {
        assertTrue(RobotsTxtPolicy.isAllowed("User-agent: *\nDisallow:", BOT, "/anything"));
    }

    @Test
    @DisplayName("A group naming this bot overrides the wildcard group")
    void specificGroupWins() {
        String robots = "User-agent: *\nDisallow: /\n\nUser-agent: SaaSquatchLeadGenBot\nAllow: /\n";
        assertTrue(RobotsTxtPolicy.isAllowed(robots, BOT, "/"));
        String blocked = "User-agent: *\nAllow: /\n\nUser-agent: saasquatchleadgenbot\nDisallow: /\n";
        assertFalse(RobotsTxtPolicy.isAllowed(blocked, BOT, "/"));
    }

    @Test
    @DisplayName("Longest matching rule wins and Allow beats Disallow on ties")
    void longestMatchWins() {
        String robots = "User-agent: *\nDisallow: /private\nAllow: /private/public\n";
        assertFalse(RobotsTxtPolicy.isAllowed(robots, BOT, "/private/data"));
        assertTrue(RobotsTxtPolicy.isAllowed(robots, BOT, "/private/public/page"));
        assertTrue(RobotsTxtPolicy.isAllowed(robots, BOT, "/about"));
    }

    @Test
    @DisplayName("Wildcards and end anchors are honored; comments are ignored")
    void wildcardsAndComments() {
        String robots = "# comment\nUser-agent: *\nDisallow: /*.pdf$ # no pdfs\n";
        assertFalse(RobotsTxtPolicy.isAllowed(robots, BOT, "/files/report.pdf"));
        assertTrue(RobotsTxtPolicy.isAllowed(robots, BOT, "/files/report.pdf.html"));
    }
}
