package com.test.security.exception;

import org.springframework.security.core.AuthenticationException;


/**
 * @author Pichat morgan
 *
 * 20 Juillet 2019
 */
public class JwtExpiredTokenException extends AuthenticationException {
    private static final long serialVersionUID = -5959543783324224864L;
    
    

    public JwtExpiredTokenException(String msg) {
        super(msg);
    }

    public JwtExpiredTokenException(String msg, Throwable t) {
        super(msg, t);
    
    }

}
