package org.example.securityproject.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.example.securityproject.repository.UserRepository;
import org.example.securityproject.util.TokenUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private TokenUtils tokenUtils;

    private UserDetailsService userDetailsService;
    private static final Log LOGGER = LogFactory.getLog(TokenAuthenticationFilter.class);
    private UserRepository userRepository;


    public TokenAuthenticationFilter(TokenUtils tokenHelper, UserDetailsService userDetailsService, UserRepository userRepository) {
        this.tokenUtils = tokenHelper;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {


        String username;

        // 1. Preuzimanje JWT tokena iz zahteva
        String authToken = tokenUtils.getToken(request);
        LOGGER.debug("Request received with token: " + authToken);
        
        try {

            if (authToken != null) {
                // 2. Citanje korisnickog imena iz tokena
				username = tokenUtils.getUsernameFromToken(authToken);
				
				if (username != null) {
					
					// 3. Preuzimanje korisnika na osnovu username-a
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					
					// 4. Provera da li je prosledjeni token validan
					if (tokenUtils.validateToken(authToken, userDetails)) {
						
						// 5. Kreiraj autentifikaciju
						TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
						authentication.setToken(authToken);
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
			}
			
		} catch (ExpiredJwtException ex) {
			LOGGER.debug("Token expired!");
		} 
		
		// prosledi request dalje u sledeci filter
		chain.doFilter(request, response);
	}
    
}
