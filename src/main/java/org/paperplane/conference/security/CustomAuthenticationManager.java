package org.paperplane.conference.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {
    private final JwtDecoder jwtDecoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof BearerTokenAuthenticationToken)) {
            System.out.println("BAD CREDENTIALS");
        }

        try {
            var jwt = jwtDecoder.decode(((BearerTokenAuthenticationToken) authentication).getToken());
            var jwtAuthenticationToken = new JwtAuthenticationToken(jwt);

            return new CustomAuthentication(jwtAuthenticationToken);
        } catch (JwtException exception) {
            throw new BadCredentialsException("Bad credentials");
        }
    }
}
