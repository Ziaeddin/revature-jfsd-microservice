package com.zia.auth.service.config;

import com.zia.auth.service.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // disabled for stateless REST endpoints (curl/Postman). Re-enable if you need browser CSRF protection.
            .authorizeHttpRequests((auth) ->
                   // auth.anyRequest().authenticated()
                    auth.requestMatchers(HttpMethod.GET,"/**").permitAll()
                            .anyRequest().authenticated()
            ).httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        // Use User.builder() and the builder's passwordEncoder to ensure passwords are encoded exactly once.
//        UserDetails user1 = User.builder()
//                .username("user")
//                .passwordEncoder(passwordEncoder()::encode)
//                .password("user123")
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User.builder()
//                .username("admin")
//                .passwordEncoder(passwordEncoder()::encode)
//                .password("admin123")
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1, admin);
//    }
}
