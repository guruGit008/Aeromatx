package com.aeromatx.back.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppRedirectionController {

    // If you need a direct path to the splash page without authentication
    // (though typically it's hit after successful login)
    @GetMapping("/splash")
    public String showSplashPage() {
        // Since splash.html is in static, Spring Boot will serve it directly
        // No need for a template name here, just return the redirect to the static file
        return "redirect:/splash.html"; // Redirects to the static HTML file
    }

    // This method is less crucial if you're using Spring Security's default success URL
    // but useful if you need to manually redirect to index.html from a controller
    @GetMapping("/index")
    public String showIndexPage() {
        return "redirect:/index.html"; // Redirects to the static HTML file
    }

    // Assuming you have a login.html in static
    @GetMapping("/login")
    public String showLoginPage() {
        return "login.html"; // Directly serve the login HTML
    }
}