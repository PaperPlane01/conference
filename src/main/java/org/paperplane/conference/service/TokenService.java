package org.paperplane.conference.service;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.model.User;
import org.paperplane.conference.model.UserRole;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;

    public String createJwtToken(User user) {
        var jwtClaimSet = JwtClaimsSet.builder()
                .claim("id", user.getId())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles().stream().map(UserRole::getName).toList())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(1L, ChronoUnit.DAYS))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimSet)).getTokenValue();
    }
}
