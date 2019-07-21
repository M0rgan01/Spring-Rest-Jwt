package com.test.security.response;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumération des codes d'erreurs
 * 
 * @author pichat morgan
 *
 * 20 Juillet 2019
 *
 */
public enum ErrorCode {
	
    GLOBAL(2),
    REGISTRATION(9),
    AUTHENTICATION(10),    
    JWT_TOKEN_EXPIRED(11),
	JWT_TOKEN_INVALID(12);
	
	
    private int errorCode;

    private ErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @JsonValue
    public int getErrorCode() {
        return errorCode;
    }
}
