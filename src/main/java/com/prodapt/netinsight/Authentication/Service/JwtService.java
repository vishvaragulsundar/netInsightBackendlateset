package com.prodapt.netinsight.Authentication.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService is a service class that provides JWT token generation and validation functionality.
 */
@Component
public class JwtService {

    Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${token.timeout}")
    private Integer timeout;

    /**
     * The secret key used for signing the JWT tokens.
     * 256-bit-secret
     */
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    /**
     * Extracts the username from a JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from a JWT token using the provided claims resolver function.
     *
     * @param token          the JWT token
     * @param claimsResolver the function to resolve the desired claim from the token's claims
     * @param <T>            the type of the claim value
     * @return the resolved claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token the JWT token
     * @return the claims extracted from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if a JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates a JWT token by checking its validity and expiration.
     *
     * @param token        the JWT token
     * @param userDetails the UserDetails object representing the authenticated user
     * @return true if the token is valid and not expired, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        logger.debug("inside token validation");
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));   
    }

    /**
     * Generates a JWT token for the specified username.
     *
     * @param userName the username
     * @return the generated JWT token
     */
    public JSONObject generateToken(String userName){
        Map<String,Object> claims=new HashMap<>();
        return createToken(claims,userName);
    }

    /**
     * Creates a JWT token with the specified claims and subject (username).
     *
     * @param claims   the claims to include in the token
     * @param userName the subject (username) of the token
     * @return the created JWT token
     */
    private JSONObject createToken(Map<String, Object> claims, String userName) {
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+timeout))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();

        JSONObject response = new JSONObject();
        response.put("status", "Success");
        response.put("token", token);
        response.put("issuedAt", new Date(System.currentTimeMillis()));
        response.put("expiresAt", new Date(System.currentTimeMillis()+timeout));
        response.put("timeoutInSeconds", timeout/1000);
        return response;
    }

    /**
     * Retrieves the signing key used for signing and validating the JWT tokens.
     *
     * @return the signing key as a Key object
     */
    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}