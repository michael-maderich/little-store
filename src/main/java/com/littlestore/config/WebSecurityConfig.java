package com.littlestore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // only needed if you're injecting it manually elsewhere
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // keep CSRF on for normal form posts
            .csrf(withDefaults())

            // authorization rules
            .authorizeHttpRequests(auth -> auth
                // public endpoints
                .antMatchers(
                        "/403",
                        "/category/**",
                        "/dollarama",
                        "/forgotPassword",
                        "/images",
                        "/index",
                        "/login",
                        "/newitems",
                        "/printOrder/**",
                        "/resendConfirmation/**",
                        "/resetPassword",
                        "/sale",
                        "/search",
                        "/searchresults",
                        "/signup"
                ).permitAll()

                // TODO: lock down admin pages
                .antMatchers("/admin/**").hasRole("ADMIN")

                // everything else requires login
                .anyRequest().authenticated()
            )

            // custom login page
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )

            // logout config
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .permitAll()
            )

            // if you still need HTTP Basic for an API
            .httpBasic(withDefaults());

        return http.build();
    }

    // static resources should be completely ignored by Spring Security
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .antMatchers(
                "/resources/**",
                "/static/**",
                "/css/**",
                "/js/**",
                "/images/**"
            );
    }
}
