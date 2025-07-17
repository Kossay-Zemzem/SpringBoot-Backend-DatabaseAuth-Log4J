package com.project.ProjectYC.controllers;

import com.project.ProjectYC.models.AuthToken;
import com.project.ProjectYC.models.UserCred;
import com.project.ProjectYC.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;


@RestController
public class AuthController {
    @Autowired
    TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;
    public static final String authToken = "auth_token"; // Name of the cookie that will be used for authentication token
    private static final Logger LG = LogManager.getLogger(); // Add a logger for logging purposes using Log4j

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserCred user, HttpServletRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Create session and store authentication
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            //returning a response with a token (for auth persistance)
            String tokenValue = UUID.randomUUID().toString(); // Generate a random token

            // Create a cookie to be send with the response, in the header of the http response
            ResponseCookie authCookie = ResponseCookie.from(authToken) // Create a cookie named "auth_token"
                    .value(tokenValue) // Set the token value
                    .maxAge(Duration.ofDays(365)) // Set cookie expiration to 1 year
                    .secure(true) // cookie is only sent over HTTPS (or localhost for development)
                    .path("/") // Cookie can be used by everything under the root path
                    .sameSite("Lax") //default is lax anyways so not necessary
                    .domain("localhost") // defines where the cookie is accessible, By default, a cookie is accessible only at the domain that set it, so not necessary to set it
                    .httpOnly(true) // Prevents JavaScript access to the cookie using Document.cookie (Cross-Site Request Falsification (CSRF) protection)
                    .build();


            tokenService.ajouterToken(new AuthToken(tokenValue));

            LG.info("[i] LOGIN: Token generated and saved to DB: " + tokenValue);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, authCookie.toString()) //add the cookie baked earlier to the response header (as a string)
                    //.body(Map.of("success", true, "token", token));
                    .body(Map.of("success", true));

        } catch (AuthenticationException e) {
            return ResponseEntity.ok().body(Map.of("success", false, "message", "Authentication failed"));
        }
    }

    @GetMapping("/authVerify")
    public Boolean authVerify(@CookieValue(value = authToken, required = false) String ClientAuthToken) {
        LG.info("[i] Token recieved from client: " + ClientAuthToken);
        // Handle null/missing cookie
        if (ClientAuthToken == null || ClientAuthToken.isEmpty()) {
            LG.info("[!] No auth token provided");
            return false;
        }

        if (tokenService.chercherToken(ClientAuthToken))
            return true;
        else {
            LG.warn("[!] Auth token is rejected (falsified or expired token) ");
            return false;
        }
    }
    @PostMapping("/logout")
    public Boolean logout(@CookieValue(value = authToken, required = true) String ClientAuthToken, HttpServletResponse response) {
        if (tokenService.chercherToken(ClientAuthToken)) {
            if (tokenService.supprimerToken(ClientAuthToken))
                LG.info("[i] Token removed successfully");
            else
                LG.error("[!] Token failed to remove (not found ?)");

            // Update the "auth_cookie" cookie to expire immediately , the new replacement cookie will be set in the header of the http response
            ResponseCookie authCookieAutoDestroy = ResponseCookie.from(authToken) // Create a cookie named "auth_token"
                    .value("Burnt_cookie") // value doesn't matter since it will be deleted
                    .maxAge(0) // Set cookie expiration 0 so it gets removed from the browser
                    .secure(true) // the other attributes NEED TO MATCH
                    .path("/")  // the other attributes NEED TO MATCH
                    .sameSite("Lax") // the other attributes NEED TO MATCH
                    .domain("localhost") // the other attributes NEED TO MATCH
                    .httpOnly(true) // the other attributes NEED TO MATCH
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, authCookieAutoDestroy.toString());

            SecurityContextHolder.clearContext();

        }
        return true;
    }

}
