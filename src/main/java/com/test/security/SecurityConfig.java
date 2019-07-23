package com.test.security;

import java.util.Arrays;
import java.util.List;

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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.security.auth.SkipPathRequestMatcher;
import com.test.security.auth.handler.RestAuthenticationEntryPoint;
import com.test.security.auth.jwt.JwtAuthenticationProvider;
import com.test.security.auth.jwt.JwtTokenAuthenticationProcessingFilter;
import com.test.security.auth.login.LoginAuthenticationProvider;
import com.test.security.auth.login.LoginProcessingFilter;
import com.test.security.token.JwtService;

/**
 * Configuration de spring security
 * 
 * 
 * @author pichat morgan
 *
 * 20 Juillet 2019
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ObjectMapper objectMapper; // manipulation d'objet JSON
	@Autowired
	private JwtService jwtService; // manipulation de JWT
	@Autowired
	private LoginAuthenticationProvider loginAuthenticationProvider; // processus d'authentification par login
	@Autowired
	private JwtAuthenticationProvider jwtAuthenticationProvider; // processus d'authentification par jwt
	@Autowired
	private RestAuthenticationEntryPoint authenticationEntryPoint; // echec d'authentification par erreur personnalisé
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private AuthenticationSuccessHandler successHandler; // succès d'authentification personnalisé
	@Autowired
	private AuthenticationFailureHandler failureHandler; // succès d'authentification par mauvais login/JWT personnalisé
		
	/**
	 * 	Création du filtre d'authentification par login
	 * 
	 * @param loginEntryPoint --> URL de login
	 * @return LoginProcessingFilter --> filtre configuré
	 * @throws Exception
	 */
	protected LoginProcessingFilter buildLoginProcessingFilter(String loginEntryPoint) throws Exception {
		LoginProcessingFilter filter = new LoginProcessingFilter(loginEntryPoint, successHandler, failureHandler,
				objectMapper);
		filter.setAuthenticationManager(this.authenticationManager);
		return filter;
	}

	/**
	 * Création du filtre d'authentification par jwt
	 * 
	 * @param pathsToSkip --> URL non utilisé pour le filtre
	 * @param pattern --> URL utilisé pour le filtre
	 * @return JwtTokenAuthenticationProcessingFilter --> filtre configuré
	 * @throws Exception
	 */
	protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter(
			List<String> pathsToSkip, String pattern) throws Exception {
		SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, pattern);
		JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(failureHandler,
				jwtService, matcher);
		filter.setAuthenticationManager(this.authenticationManager);
		return filter;
	}

	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// processus d'authentification par login
		auth.authenticationProvider(loginAuthenticationProvider);
		// processus d'authentification par jwt
		auth.authenticationProvider(jwtAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	
		// list des url d'authentification
		List<String> permitAllEndpointList = Arrays.asList(
				SecurityConstants.AUTHENTICATED_URL	           
	        );
		
		// prend en compte la config cors initialiser dans la config des beans
		http.cors();

		// desactive la protection par token generere automatiquement pour la faille
		// crrf
		http.csrf().disable() .exceptionHandling()
        .authenticationEntryPoint(this.authenticationEntryPoint);

		// desacive la creation de session par spring
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		//requete ne nécésitant aucune authentification
		http.authorizeRequests().antMatchers(permitAllEndpointList.toArray(new String[permitAllEndpointList.size()])).permitAll();
		
		//requete nécésitant un Role particulier
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/tasks/**").hasAnyAuthority("ROLE_ADMIN");
		
		//requete nécésitant une authentification
		http.authorizeRequests().anyRequest().authenticated();
		
		//filtre pour le processus de login initial, s'effectue uniquement à l'adresse indiqué en param 1
		http.addFilterBefore(buildLoginProcessingFilter(SecurityConstants.AUTHENTICATION_URL), UsernamePasswordAuthenticationFilter.class);
		
		//filtre pour le processus de login par JWT, ne s'éffectue par pour les adresse en params 1, et est utilisé pour les adresse en params 2
		http.addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(permitAllEndpointList, SecurityConstants.API_ROOT_URL), UsernamePasswordAuthenticationFilter.class);
		
	}

	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	// indique les autorisation de lecture des Headers --> sans ces indications, le
	// client (Angular) n'y a pas accès
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// indique les url autorisé
		configuration.setAllowedOrigins(Arrays.asList("*"));
		// méthode autorisé
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		// en-tête autorisé
		configuration.setAllowedHeaders(Arrays.asList("Origin", "Accept", "X-Requested-With", "Content-Type",
				"Access-Control-Request-Method", "Authorization", "Access-Control-Request-Headers", "refresh"));
		// en-tête exposé
		configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Authorization", "refresh",
				"Access-Control-Allow-Credentials"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// les adresses utilisé avec la configuration cors
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}


}
