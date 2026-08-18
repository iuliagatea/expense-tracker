package org.example.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Extract the token from the Authorization header
        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String token = null;

        // Check if the Authorization header is present and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove "Bearer " prefix to get the token
            try {
                // Extract the email from the token
                email = jwtUtil.extractUsername(token);
            } catch (ExpiredJwtException e) {
                // Handle expired token exception
                System.out.println("JWT Token has expired");
            } catch (Exception e) {
                // Handle other exceptions
                System.out.println("Error extracting username from JWT Token");
            }
        } else {
            // If the header is missing or doesn't have Bearer prefix
            System.out.println("JWT Token does not begin with Bearer String");
        }

        // Validate the token and set the authentication context
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details from the database
            var userDetails = userDetailsService.loadUserByUsername(email);

            // Validate the token
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                // Create an authentication token
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Set additional details
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
