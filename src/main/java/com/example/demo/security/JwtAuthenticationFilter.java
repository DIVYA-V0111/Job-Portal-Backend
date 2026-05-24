package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.util.JwtUtil;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    /**
     * This method runs on EVERY request
     * It checks if the request has a valid JWT token
     */
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        // existing JWT logic below
        
        // 1. Extract Authorization header
        String authHeader = request.getHeader("Authorization");
        
        String token = null;
        String email = null;
        
        // 2. Check if header contains Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove "Bearer " prefix
            
            try {
                // 3. Extract email from token
                email = jwtUtil.extractEmail(token);
            } catch (Exception e) {
                // Token is invalid (expired, malformed, etc.)
                logger.error("JWT Token extraction failed: " + e.getMessage());
            }
        }
        
        // 4. If email exists and user is not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 5. Load user details from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            
            // 6. Validate token
            if (jwtUtil.validateToken(token, email)) {
                
                // 7. Create authentication object
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                
                // 8. Set additional details
                authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // 9. Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        
        // 10. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}