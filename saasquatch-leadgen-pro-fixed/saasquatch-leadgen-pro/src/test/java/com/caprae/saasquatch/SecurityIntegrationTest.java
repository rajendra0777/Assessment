package com.caprae.saasquatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Boots the whole app on in-memory H2 and checks that the API is actually locked down. */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"app.seed.password=TestSeedPass123", "app.jwt.secret="})
class SecurityIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private String loginToken(String user, String pass) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode json = mapper.readTree(body);
        return json.get("token").asText();
    }

    @Test
    @DisplayName("Unauthenticated calls to protected APIs get 401")
    void anonymousIsRejected() throws Exception {
        mvc.perform(get("/api/leads")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/metrics/dashboard")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/scraper/extract").contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"example.com\"}")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("The old hardcoded password is rejected at the HTTP layer")
    void backdoorPasswordRejected() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"Caprae2025!\"}")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("An operator can read leads but cannot delete them or create accounts")
    void roleBasedAccess() throws Exception {
        String token = loginToken("searcher", "TestSeedPass123");
        mvc.perform(get("/api/leads").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        mvc.perform(delete("/api/leads/1").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/register").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newuser\",\"email\":\"n@example.com\",\"password\":\"a-long-password-1\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("The scraper refuses internal addresses (SSRF)")
    void scraperBlocksInternalTargets() throws Exception {
        String token = loginToken("searcher", "TestSeedPass123");
        mvc.perform(post("/api/scraper/extract").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"http://169.254.169.254/latest/meta-data/\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Health stays public, other actuator endpoints do not")
    void actuator() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
        mvc.perform(get("/actuator/metrics")).andExpect(status().isUnauthorized());
    }
}
