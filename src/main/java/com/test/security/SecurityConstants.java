package com.test.security;

public class SecurityConstants {

	public static final String SECRET = "secret";
	public static final long EXPIRATION_TIME = 864_000_000; //10 jours
	public static final String TOKEN_PREFIX = "bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String AUTHORITIES_PREFIX = "roles";
}
