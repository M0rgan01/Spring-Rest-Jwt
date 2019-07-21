package com.test.security.token;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.test.security.auth.model.UserContext;

/**
 * Une simple implementation de {@link org.springframework.security.core.Authentication},
 * qui aura pour role une simple présentation de JwtToken.
 * 
 * @author pichat morgan
 *
 * 20 Juillet 2019
 *
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 2877954820905567501L;

    private JwtToken jwtToken;
    private UserContext userContext;

    public JwtAuthenticationToken(JwtToken unsafeToken) {
        super(null);
        this.jwtToken = unsafeToken;
        this.setAuthenticated(false);
    }

    public JwtAuthenticationToken(UserContext userContext, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.eraseCredentials();
        this.userContext = userContext;
        super.setAuthenticated(true);
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return jwtToken;
    }

    @Override
    public Object getPrincipal() {
        return this.userContext;
    }

    @Override
    public void eraseCredentials() {        
        super.eraseCredentials();
        this.jwtToken = null;
    }
}
