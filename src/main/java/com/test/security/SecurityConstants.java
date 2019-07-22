package com.test.security;

import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:authentication.properties")
public class SecurityConstants {

	//////////////////// TOKEN //////////////////

	//@Value("${token.secret}")
	public static final String SECRET = "secret";
	//@Value("${token.refresh.expiration.toMillis}")
	public static final long EXPIRATION_TIME_REFRESH_TOKEN = 864_000_000;
	//@Value("${token.auth.expiration.toMillis}")
	public static final long EXPIRATION_TIME_AUTH_TOKEN = 300_000;
	//@Value("${token.prefix}")
	public static final String TOKEN_PREFIX = "bearer "; // préfix que apparait avant le jwt
	//@Value("${token.header.auth}")
	public static final String HEADER_AUTH_STRING = "Authorization"; // nom du header du JWT pour l'auth
	//@Value("${token.header.refresh}")
	public static final String HEADER_REFRESH_STRING = "refresh"; // nom du header du JWT pour le refresh
	//@Value("${token.authorities.prefix}")
	public static final String AUTHORITIES_PREFIX = "roles"; // nom du champs de la liste des roles dans le payload
	//@Value("${token.active.prefix}")
	public static final String REFRESH_ACTIVE_PREFIX = "active"; // nom du champs de la vérification pour le refresh
																	// dans le payload

	//////////////////// PATH //////////////////
	//@Value("${url.authenticated}")
	public static final String AUTHENTICATED_URL = "/api/auth/**"; // regroupement des URL d'authentification
	//@Value("${url.authentication}")
	public static final String AUTHENTICATION_URL = "/api/auth/login"; // URL de login
	//@Value("${url.refresh.token}")
	public static final String REFRESH_TOKEN_URL = "/api/auth/token"; // URL de rafraichissement de token
	//@Value("${url.api.root}")
	public static final String API_ROOT_URL = "/api/**";

}
