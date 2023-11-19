package com.prodapt.netinsight.Authentication.filter;

import javax.naming.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import com.prodapt.netinsight.Authentication.Service.JwtService;
import com.prodapt.netinsight.Authentication.Service.UserInfoDetailsService;

/**
 * JwtAuthFilter is a filter that intercepts incoming requests and validates the JWT token in the Authorization header.
 * It retrieves the username from the token and performs authentication and authorization checks.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserInfoDetailsService userInfoDetailsService;

    /**
     * Filters the incoming requests, validates the JWT token, and sets the authenticated user in the SecurityContextHolder.
     *
     * @param request     the incoming HttpServletRequest
     * @param response    the outgoing HttpServletResponse
     * @param filterChain the filter chain for continuing the request processing
     * @throws ServletException if an error occurs during the request processing
     * @throws IOException      if an I/O error occurs during the request processing
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("Processing request: " + request.getRequestURL().toString());
        String authHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: " + authHeader);
        String token = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.debug("Authorization header starts with Bearer. Extracting token.");
            token = authHeader.substring(7);
            try {
                username = jwtService.extractUsername(token);
            } catch (io.jsonwebtoken.security.SignatureException ex) {
                // If exception occurs when extracting username, send an unauthorized error
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                // If exception occurs when token is expired, send an unauthorized error
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Expired");
                return;
            } catch (io.jsonwebtoken.io.DecodingException e) {
                // If exception occurs when token is invalid, send an unauthorized error
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("User not authenticated. Authenticating user: " + username);
            UserDetails userDetails = userInfoDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        logger.debug("Request processing completed.");

        // Continue the filter chain for further request processing
        filterChain.doFilter(request, response);
    }

}
