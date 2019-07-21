package com.test.security.token;

import org.springframework.security.authentication.BadCredentialsException;

import com.test.security.SecurityConstants;
import com.test.security.exception.JwtExpiredTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;


/**
 * Représention d'un JWT pour l'authentification
 * 
 * @author pichat morgan
 *
 * 20 Juillet 2019
 *
 */
public class JwtToken {

	private String token;
	  	
	public JwtToken(String token) {
		super();
		this.token = token;
	}

	public String getToken() {		
		return this.token;
	}
	
	 /**
     * Parses and validates JWT Token signature.
     * 
     * @throws BadCredentialsException
     * @throws JwtExpiredTokenException
     * 
     */
    public Jws<Claims> parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""));
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            //logger.error("Invalid JWT Token", ex);
            throw new BadCredentialsException("Invalid JWT token: ", ex);
        } catch (ExpiredJwtException expiredEx) {
           // logger.info("JWT Token is expired", expiredEx);
            throw new JwtExpiredTokenException( "JWT Token expired", expiredEx);
        }
    }
}
