package com.project.ProjectYC.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    DataSource dataSource; //automatically injected by Spring Boot , no need to configure it manually
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // Enable sessions
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers("/login","/authVerify","/logout").permitAll() // Allow /login without authorization (API authorization)
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow all OPTIONS requests
                                .anyRequest().authenticated()
                )
                .logout(logout -> logout.disable()) // Disable Spring Security's default logout (to be able to use the logout endpoint)
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // CORS configuration to allow requests from the Angular frontend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // Allow Angular frontend origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow necessary HTTP methods
        configuration.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Allow credentials (e.g., cookies, authorization headers)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints
        return source;
    }

    // This method defines the users and their roles in the application
    // Warning : this is for testing purposes only , do not hardcode passwords in production !
    @Bean
    public UserDetailsService userDetailsService() {
        // Define users in a list for easier management
        List<UserDetails> initialUsers = List.of(
        //User 1 : password of user "user" stored in plain text as "usertest@12345"
         User.withUsername("user").
                 password("{noop}usertest@12345").
                 roles("USER").
                 build(),
        //User 2 : password  of user "admin" stored in plain text as "zzz"
        User.withUsername("admin").
                password("{noop}zzz").
                roles("MODERATOR").
                build(),

        //User 3 : Admin is stored with a hashed password (using Bcript Hash) , this is indicated with {bcrypt}
        // (again, only for testing purposes,not secure for production)

        User.withUsername("admin2").
                password("{bcrypt}$2a$08$AMF6CI6Qi8uoLXtVYJXZo.AKXDwaxyh6nCykX7xqkRXL6bA2mA8l.").
                roles("ADMIN").
                build()
        );
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        // Create users if they don't exist
        initialUsers.forEach(user -> {
            if (!userDetailsManager.userExists(user.getUsername())) {
                userDetailsManager.createUser(user);
            }
        });

        return userDetailsManager; //users will be stored in the database using JdbcUserDetailsManager instead of in Memory

    }

}
