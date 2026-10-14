package com.caprae.saasquatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaRouteController {

    @GetMapping({
        "/login",
        "/deal-pipeline",
        "/deal-pipeline/**",
        "/real-time-sourcing",
        "/real-time-sourcing/**",
        "/ai-deal-copilot",
        "/ai-deal-copilot/**",
        "/outreach-studio",
        "/outreach-studio/**",
        "/pipeline-analytics",
        "/pipeline-analytics/**",
        "/api-docs",
        "/api-and-docs"
    })
    public String forwardSpaRoutes() {
        return "forward:/index.html";
    }
}
