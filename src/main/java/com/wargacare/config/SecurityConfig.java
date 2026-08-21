package com.wargacare.config;

import com.wargacare.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/announcements/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reports/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reports/statuses").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reports/stream").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/me").authenticated()
                        .requestMatchers("/api/dashboard/**").hasRole("ADMIN_RT")
                        .requestMatchers("/api/users/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.POST, "/api/announcements/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.PUT, "/api/announcements/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.PATCH, "/api/announcements/*/pin").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.DELETE, "/api/announcements/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.POST, "/api/events/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.PUT, "/api/events/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.POST, "/api/upload").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/chat").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reports/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reports").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.PATCH, "/api/reports/*/status").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.GET, "/api/kas/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/kas/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.PUT, "/api/kas/**").hasRole("ADMIN_RT")
                        .requestMatchers(HttpMethod.DELETE, "/api/kas/**").hasRole("ADMIN_RT")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
