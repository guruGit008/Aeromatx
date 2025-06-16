package com.aeromatx.back.config;

import com.aeromatx.back.security.JwtAuthenticationEntryPoint;
import com.aeromatx.back.security.JwtAuthenticationFilter;
import com.aeromatx.back.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity; // Import WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer; // Import WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize, @PostAuthorize annotations
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          JwtAuthenticationEntryPoint unauthorizedHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Configure paths to be completely ignored by Spring Security filter chain
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/", // Root path
            "/*.html", // All HTML files at the root level (index.html, login.html, register.html, reset-password.html, etc.)
            "/*.css", // All CSS files at the root level
            "/*.css2", // All CSS files at the root level
            "/*.js", // All JS files at the root level
            "/*.js2", // All JS files at the root level
            "/css/**", // All files under /css/
            "/css2/**", // All files under /css/
            "/js/**", // All files under /js/
            "/js2/**", // All files under /js/
            "/img/**", // All files under /img/
            "/lib/**", // All files under /lib/
            "/favicon.ico", // Favicon
            "/fonts/**", // All files under /js/
            "/error", // The default Spring Boot error page
            "/admin_pannel/**",// All files under /admin_pannel/
            "/admin_pannel/css/**", // All files under /admin_pannel/css/
            "/admin_pannel/js/**" // All files under /admin_pannel/css/
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF for stateless REST APIs
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Handle unauthorized access
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions for JWT
            .authorizeHttpRequests(auth -> auth
                // Permit all /api/auth endpoints for public access (registration, login, forgot/reset password API calls)
                .requestMatchers("/api/auth/**").permitAll() 
                // TEMPORARY: Allow all product endpoints for quick testing (consider securing these later)
                .requestMatchers("/api/products/**").permitAll() 
                // All other /api endpoints should require authentication
                .requestMatchers("/api/**").authenticated() 
                // Any other request that hasn't been matched by previous rules should also be authenticated.
                .anyRequest().authenticated() 
            );

        http.authenticationProvider(authenticationProvider()); // Set up authentication provider
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before UsernamePasswordAuthenticationFilter

        return http.build();
    }
}
