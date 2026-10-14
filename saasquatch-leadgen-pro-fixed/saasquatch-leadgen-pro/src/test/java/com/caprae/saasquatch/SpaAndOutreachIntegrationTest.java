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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"app.seed.password=TestSeedPass123", "app.jwt.secret="})
class SpaAndOutreachIntegrationTest {

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
    @DisplayName("SPA route endpoints are accessible publicly and forward to index.html")
    void spaRoutesArePubliclyAccessible() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk());
        mvc.perform(get("/deal-pipeline")).andExpect(status().isOk());
        mvc.perform(get("/deal-pipeline/CP-0005")).andExpect(status().isOk());
        mvc.perform(get("/deal-pipeline/qualified")).andExpect(status().isOk());
        mvc.perform(get("/deal-pipeline/loi-submitted")).andExpect(status().isOk());
        mvc.perform(get("/real-time-sourcing")).andExpect(status().isOk());
        mvc.perform(get("/ai-deal-copilot")).andExpect(status().isOk());
        mvc.perform(get("/ai-deal-copilot/financial-model")).andExpect(status().isOk());
        mvc.perform(get("/outreach-studio")).andExpect(status().isOk());
        mvc.perform(get("/outreach-studio/CP-0005")).andExpect(status().isOk());
        mvc.perform(get("/pipeline-analytics")).andExpect(status().isOk());
        mvc.perform(get("/api-docs")).andExpect(status().isOk());
        mvc.perform(get("/api-and-docs")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Outreach send and draft endpoints function with authentication")
    void outreachEndpointsWork() throws Exception {
        String token = loginToken("searcher", "TestSeedPass123");

        // Test sending outreach email
        mvc.perform(post("/api/ai/outreach/send")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipient\":\"founder@example.com\",\"subject\":\"Strategic Partnership\",\"body\":\"Hi there...\",\"touch\":1,\"leadId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.recipientEmail").isNotEmpty())
                .andExpect(jsonPath("$.trackingId").isNotEmpty());

        // Test saving draft
        mvc.perform(post("/api/ai/outreach/draft")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipient\":\"founder@example.com\",\"subject\":\"Draft Subject\",\"body\":\"Draft body...\",\"touch\":2,\"leadId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT_SAVED"))
                .andExpect(jsonPath("$.touch").value(2));

        // Test unauthenticated access is rejected
        mvc.perform(post("/api/ai/outreach/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipient\":\"founder@example.com\",\"subject\":\"Partnership\",\"body\":\"Hi...\",\"touch\":1}"))
                .andExpect(status().isUnauthorized());
    }
}
