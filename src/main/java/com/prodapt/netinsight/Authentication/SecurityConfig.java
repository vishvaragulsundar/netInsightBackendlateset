package com.prodapt.netinsight.Authentication;


import com.prodapt.netinsight.Authentication.Service.UserInfoDetailsService;
import com.prodapt.netinsight.Authentication.filter.JwtAuthFilter;
import com.prodapt.netinsight.Authentication.filter.JwtAuthendpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;

/**
 * Configuration class for Spring Security.
 * This class defines the security configuration settings, authentication, authorization, and access control rules.
 */
@Configuration
@EnableMethodSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    @Autowired
    private JwtAuthFilter authFilter;

    @Autowired
    private JwtAuthendpoint jwtAuthendpoint;
    /**
     * Configures the UserDetailsService bean for authentication.
     * @return An instance of UserInfoDetailsService to handle user details retrieval for authentication.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserInfoDetailsService();
    }

    /**
     * Configures the PasswordEncoder bean for password hashing.
     * @return An instance of BCryptPasswordEncoder for password encoding.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the SecurityFilterChain for defining security rules.
     * @param http The HttpSecurity object to configure security settings.
     * @return The configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // todo: /auth/adduser should be restricted or there has to be mechanism to approve any new user registration
        return http.cors().and()
                .csrf().disable()
                .authorizeHttpRequests()
                .antMatchers("/auth/adduser",
                        "/h2-console",
                        "/authenticate",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/webjars/**").permitAll() // Allow anyone to access these endpoints
                .and()
                .authorizeHttpRequests().antMatchers("/**").authenticated() // Require authentication for all other endpoints
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthendpoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures the AuthenticationProvider for authenticating user credentials.
     * @return An instance of DaoAuthenticationProvider configured with the UserDetailsService and PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Creates an instance of the AuthenticationManager using the provided AuthenticationConfiguration.
     *
     * @param config the AuthenticationConfiguration used to configure the AuthenticationManager
     * @return the created AuthenticationManager instance
     * @throws Exception if an error occurs while creating the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
