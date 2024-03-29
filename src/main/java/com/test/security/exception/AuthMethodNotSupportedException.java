package com.test.security.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * @author Pichat morgan
 *
 * 20 Juillet 2019
 */
public class AuthMethodNotSupportedException extends AuthenticationServiceException {
    private static final long serialVersionUID = 3705043083010304496L;

    public AuthMethodNotSupportedException(String msg) {
        super(msg);
    }
}
