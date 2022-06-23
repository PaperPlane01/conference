package org.paperplane.conference.service;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.api.request.LoginRequest;
import org.paperplane.conference.api.response.LoginResponse;
import org.paperplane.conference.model.User;
import org.paperplane.conference.repository.UserRepository;
import org.paperplane.conference.security.CustomAuthentication;
import org.paperplane.conference.security.CustomUserDetails;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public LoginResponse login(LoginRequest loginRequest) {
        var user = userRepository.findByUsername(loginRequest.getUsername())
                .filter(foundUser -> passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
        var jwt = tokenService.createJwtToken(user);

        return LoginResponse.builder()
                .accessToken(jwt)
                .build();
    }

    public Optional<CustomUserDetails> getCurrentUserDetails() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(authentication -> authentication instanceof CustomAuthentication)
                .map(authentication -> ((CustomAuthentication) authentication).getUserDetails());
    }

    public CustomUserDetails requireCurrentUserDetails() {
        return getCurrentUserDetails().orElseThrow(() -> new BadCredentialsException("Bad credentials"));
    }

    public Optional<User> getCurrentUser() {
        return getCurrentUserDetails().flatMap(userDetails -> userRepository.findById(userDetails.getId()));
    }

    public User requireCurrentUser() {
        return getCurrentUser().orElseThrow(() -> new BadCredentialsException("Bad credentials"));
    }
}
