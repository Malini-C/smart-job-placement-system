package com.placementsystem.config;

import com.placementsystem.security.JwtAuthenticationFilter;
import com.placementsystem.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ═══════════════════════════════════════════════════════
                // CRITICAL: DISABLE CSRF (otherwise POST fails with 403)
                // ═══════════════════════════════════════════════════════
                .csrf(csrf -> csrf.disable())

                // ═══════════════════════════════════════════════════════
                // ENABLE CORS
                // ═══════════════════════════════════════════════════════
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ═══════════════════════════════════════════════════════
                // STATELESS SESSION (for JWT)
                // ═══════════════════════════════════════════════════════
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ═══════════════════════════════════════════════════════
                // AUTHORIZATION RULES
                // ═══════════════════════════════════════════════════════
                .authorizeHttpRequests(auth -> auth

                        // ═══════════════════════════════════════════════
                        // STATIC RESOURCES — Frontend files
                        // ═══════════════════════════════════════════════
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/index.html").permitAll()
                        .requestMatchers("/login.html").permitAll()
                        .requestMatchers("/register.html").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()

                        // ✅ FIX: Use /** to match nested folders
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/static/**").permitAll()

                        // Frontend page folders (student/admin pages)
                        .requestMatchers("/student/**").permitAll()
                        .requestMatchers("/admin/**").permitAll()

                        // Root-level files
                        .requestMatchers("/*.html").permitAll()
                        .requestMatchers("/*.ico").permitAll()
                        .requestMatchers("/*.png").permitAll()
                        .requestMatchers("/*.svg").permitAll()

                        // ═══════════════════════════════════════════════
                        // AUTH ENDPOINTS (MUST BE PERMIT ALL)
                        // ═══════════════════════════════════════════════
                        .requestMatchers("/api/auth/**").permitAll()

                        // Test endpoints
                        .requestMatchers("/api/test/**").permitAll()

                        // Public read-only
                        .requestMatchers(HttpMethod.GET, "/api/companies").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/companies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/jobs").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/jobs/active").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()
                        .requestMatchers("/api/applications/count/**").permitAll()
                        .requestMatchers("/api/interviews/student/**").permitAll()

                        // ═══════════════════════════════════════════════
                        // ADMIN ENDPOINTS
                        // ═══════════════════════════════════════════════
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/companies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/companies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/jobs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/jobs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/jobs/**").hasRole("ADMIN")
                        .requestMatchers("/api/interviews/**").hasRole("ADMIN")

                        // ═══════════════════════════════════════════════
                        // STUDENT ENDPOINTS
                        // ═══════════════════════════════════════════════
                        .requestMatchers("/api/students/**").hasRole("STUDENT")

                        // All others
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:8080",
                "http://127.0.0.1:5500",
                "http://localhost:5500"
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}