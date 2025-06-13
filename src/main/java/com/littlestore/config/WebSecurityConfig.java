package com.littlestore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.session.SimpleRedirectInvalidSessionStrategy;
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
                // when the HTTP session is invalid/expired…
                .sessionManagement(management -> management
                        .invalidSessionStrategy(
                                new SimpleRedirectInvalidSessionStrategy("/login")
                        ))
                // keep CSRF on for normal form posts
                .csrf(withDefaults())

                .exceptionHandling(ex -> ex
                	    // Unauthenticated (or invalid session) → redirect to /login
                	    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                	    // Authenticated but insufficient role → show 403 page
                	    .accessDeniedPage("/403")
            	  )

                // authorization rules
                .authorizeHttpRequests(auth -> auth
                                // Static assets - permit everyone
                                .antMatchers(
                                        "/resources/**",
                                        "/static/**",
                                        "/css/**",
                                        "/js/**",
                                        "/images/**"
                                ).permitAll()

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
                                // everything under /admin/** requires one of those roles
                                .antMatchers("/admin/**", "/connect", "/oauth2/callback")
                                .hasAnyRole("OWNER", "ADMIN")
                                // everything else requires login
                                .anyRequest().authenticated()

                )

                // custom login page
                .formLogin(form -> form
                                .loginPage("/login")
                                .successHandler(successHandler())
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

    @Bean
    AuthenticationSuccessHandler successHandler() {
      return (request, response, authentication)  -> {
          boolean admin = authentication.getAuthorities().stream()
              .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") 
                          || a.getAuthority().equals("ROLE_OWNER"));
          if (admin) {
            response.sendRedirect(request.getContextPath()+"/admin/dashboard");
          } else {
            response.sendRedirect(request.getContextPath()+"/newitems");
          }
      };
    }
}
