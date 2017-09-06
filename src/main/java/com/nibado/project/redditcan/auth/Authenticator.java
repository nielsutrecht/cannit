package com.nibado.project.redditcan.auth;

import com.nibado.project.redditcan.reddit.Reddit;
import lombok.extern.slf4j.Slf4j;
import net.dean.jraw.http.oauth.OAuthException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
public class Authenticator implements AuthenticationProvider {
    private Reddit reddit;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return authenticate((UsernamePasswordAuthenticationToken) authentication);
        }

        return null;
    }

    private Authentication authenticate(UsernamePasswordAuthenticationToken token) {
        log.info("User: {}", token.getPrincipal());

        Reddit reddit = new Reddit();

        try {
            reddit.login(token.getPrincipal().toString(), token.getCredentials().toString());
        } catch (OAuthException e)  {
            log.warn("Error logging in for user {}: {}", token.getPrincipal(), e.getMessage(), e);
            return null;
        }

        this.reddit = reddit;

        return new UsernamePasswordAuthenticationToken( token.getPrincipal(), token.getCredentials(), Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }

    public Optional<Reddit> reddit() {
        return Optional.ofNullable(reddit);
    }
}
