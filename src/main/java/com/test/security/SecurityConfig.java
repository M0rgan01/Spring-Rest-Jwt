package com.test.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.business.JwtService;

/**
 * Configuration de spring security
 * 
 * 
 * @author pichat morgan
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private JwtService jwtService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// authentification par objet userDetailsService
		auth.userDetailsService(userDetailsService).passwordEncoder(getBCPE());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		//prend en compte la config cors initialiser dans la config des beans
		http.cors();
		
		// desactive la protection par token generere automatiquement pour la faille
		// crrf
		http.csrf().disable();

		// desacive la creation de session par spring
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.authorizeRequests().antMatchers("/login/**", "/register/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/tasks/**").hasAnyAuthority("ROLE_ADMIN");
		http.authorizeRequests().anyRequest().authenticated();

		// ajout du token en reponse
		// Ajout d'un filtre
		http.addFilter(new JWTAuthenticationFilter(authenticationManager(), objectMapper, jwtService));

		// vérification de l'en-tente JWT en request
		// ajoute un filtre avant la position de la classe de filtre spécifiée
		http.addFilterBefore(authorizationFilterBean(), UsernamePasswordAuthenticationFilter.class);

		// addFilterBefore vérifie s'il y a un token ainsi que sa conformité

		// addFilter authentifie via JSON et donne le token en header si le résultat est
		// correct
	}

	@Bean
	public BCryptPasswordEncoder getBCPE() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ObjectMapper getOM() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper;
	}

	@Bean
	public JWTAuthorizationFilter authorizationFilterBean() throws Exception {
		return new JWTAuthorizationFilter();
	}

	// indique les autorisation de lecture des Headers --> sans ces indications, le
	// client (Angular) n'y a pas accès
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //indique les url autorisé
        configuration.setAllowedOrigins(Arrays.asList("*"));
        // méthode autorisé
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        //en-tête autorisé
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "Authorization", "Access-Control-Request-Headers"));
        //en-tête exposé
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Authorization", "Access-Control-Allow-Credentials"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // les adresses utilisé avec la configuration cors
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
	
	
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
