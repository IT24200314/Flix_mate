package com.flixmate.flixmate.api.config;

import com.flixmate.flixmate.api.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API testing
            .authorizeHttpRequests(requests -> requests
                .requestMatchers(
                    "/", "/index.html", "/login.html", "/register.html", "/debug-login.html",
                    "/*.html", "/*.css", "/*.js", "/*.png", "/*.jpg", "/*.ico"
                ).permitAll()
                .requestMatchers("/api/**").permitAll() // Temporarily allow all API endpoints for debugging
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .formLogin(form -> form
                .loginPage("/login.html")
                .permitAll()
                .defaultSuccessUrl("/index.html", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login.html")
                .permitAll()
            )
            .httpBasic(httpBasic -> httpBasic.disable()); // Disable basic authentication popup
        return http.build();
    }
}


