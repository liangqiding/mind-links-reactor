package com.mind.links.security.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author qiDing
 * date: 2021-01-04 15:50
 * @version v1.0.0
 * description
 */
public class SpringAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
