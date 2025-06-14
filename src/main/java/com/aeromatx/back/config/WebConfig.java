package com.aeromatx.back.config;





import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Apply CORS to all /api endpoints
                .allowedOrigins(
                    "http://127.0.0.1:5500", // Example: VS Code Live Server
                    "http://localhost:3000",  // Example: React Dev Server
                    "http://localhost:4200"   // Example: Angular Dev Server
                    // Add your actual frontend URLs here.
                    // For production, specify exact domains, not '*'
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Allow common HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true) // Allow sending cookies/auth headers
                .maxAge(3600); // Max age for preflight requests (in seconds)
    }
}