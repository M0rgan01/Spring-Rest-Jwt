package com.test.security;

public class SecurityConstants {

	//////////////////// TOKEN //////////////////

	public static final String SECRET = "secret";
	public static final long EXPIRATION_TIME_REFRESH_TOKEN = 864_000_000; // 10 jours
	
	// public static final long EXPIRATION_TIME_AUTH_TOKEN = 300_000; //5 minutes
	//public static final long EXPIRATION_TIME_AUTH_TOKEN = 3_000; // 5 minutes
	public static final long EXPIRATION_TIME_AUTH_TOKEN = 864_000_000;
	
	public static final String TOKEN_PREFIX = "bearer "; // préfix que apparait avant le jwt
	public static final String HEADER_AUTH_STRING = "Authorization"; // nom du header du JWT pour l'auth
	public static final String HEADER_REFRESH_STRING = "refresh"; // nom du header du JWT pour le refresh
	public static final String AUTHORITIES_PREFIX = "roles"; // nom du champs de la liste des roles dans le payload
	public static final String REFRESH_ACTIVE_PREFIX = "active"; // nom du champs de la vérification pour le refresh
																	// dans le payload

//////////////////// PATH //////////////////

	
	public static final String AUTHENTICATED_URL = "/api/auth/**"; // regroupement des URL d'authentification
	public static final String AUTHENTICATION_URL = "/api/auth/login"; // URL de login
	public static final String REFRESH_TOKEN_URL = "/api/auth/token"; // URL de rafraichissement de token
	
	public static final String API_ROOT_URL = "/api/**";

}
