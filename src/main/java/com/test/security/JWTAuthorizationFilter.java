package com.test.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.test.business.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * verification du token en en-tente
 * 
 * @author picha
 *
 */
public class JWTAuthorizationFilter extends OncePerRequestFilter {

	@Autowired
	public JwtService jwtService;

		
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// dans le cas d'un requete OPTIONS, il valide la réponse (200-ok)
		if (request.getMethod().equals("OPTIONS")) {
			response.setStatus(HttpServletResponse.SC_OK);

		} else {

			// on recupere le token dans l'en-tete de la requete
			String jwt = request.getHeader(SecurityConstants.HEADER_STRING);

			// s'il est null ou qu'il na pas pour prefix la constante
			if (jwt == null || !jwt.startsWith(SecurityConstants.TOKEN_PREFIX)) {

				// appel du filtre suivant
				filterChain.doFilter(request, response);
			
			} else {

			// on récupère le token dans un object Claims
			Claims claims = Jwts.parser().setSigningKey(SecurityConstants.SECRET) // on assigne le secret
					.parseClaimsJws(jwt.replace(SecurityConstants.TOKEN_PREFIX, "")) // on enlève le préfixe
					.getBody(); // on récupère le corp

			// on recupere username
			String username = claims.getSubject();
		
			// on creer un object spring
			UsernamePasswordAuthenticationToken authenticationUser = new UsernamePasswordAuthenticationToken(username,
					null, jwtService.getListAuthorities(claims));

			// on l'authentifie grace au context spring
			SecurityContextHolder.getContext().setAuthentication(authenticationUser);

			// appel du filtre suivant
			filterChain.doFilter(request, response);
			}
		}
	}

}
