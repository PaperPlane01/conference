package org.paperplane.conference;

import lombok.extern.slf4j.Slf4j;
import org.paperplane.conference.security.CustomAuthentication;
import org.paperplane.conference.security.CustomUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.stream.Stream;

@Slf4j
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        log.info("Creating custom security context");

        var customUserDetails = new CustomUserDetails();
        customUserDetails.setUsername(customUser.username());
        customUserDetails.setId(customUser.id());
        customUserDetails.setRoles(
                Stream.of(customUser.roles()).map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList()
        );

        var customAuthentication = new CustomAuthentication(customUserDetails);
        customAuthentication.setAuthenticated(true);

        log.info("Setting custom authentication with roles {}", customAuthentication.getAuthorities());

        var securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(customAuthentication);

        return securityContext;
    }
}
