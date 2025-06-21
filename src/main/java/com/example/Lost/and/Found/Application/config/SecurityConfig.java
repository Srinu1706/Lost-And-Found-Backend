package com.example.Lost.and.Found.Application.config;

import com.example.Lost.and.Found.Application.Service.UserService;
import com.example.Lost.and.Found.Application.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtFilter jwtFilter;  

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/login", "/project","/verify-otp").permitAll()
                .requestMatchers("/Lost").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/getallitemdetails", "/delete/**", "/updateitem", "/reporter/**").hasRole("ADMIN")
                .anyRequest().authenticated())
//            .formLogin(form -> form
//                    .loginPage("/login-page") 
//                    .loginProcessingUrl("/process-login")
//                    .defaultSuccessUrl("/home", true)
//                    .failureUrl("/login-page?error=true")
//                    .permitAll()
//                )
//                .logout(logout -> logout
//                    .logoutUrl("/logout")
//                    .logoutSuccessUrl("/login-page?logout=true")
//                    .permitAll()
//                )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); 

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
