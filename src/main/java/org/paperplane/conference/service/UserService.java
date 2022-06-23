package org.paperplane.conference.service;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.api.request.SignUpRequest;
import org.paperplane.conference.api.response.SignUpResponse;
import org.paperplane.conference.api.response.UserResponse;
import org.paperplane.conference.exception.NotFoundException;
import org.paperplane.conference.exception.UsernameIsAlreadyInUseException;
import org.paperplane.conference.mapper.UserMapper;
import org.paperplane.conference.model.Role;
import org.paperplane.conference.model.User;
import org.paperplane.conference.model.UserRole;
import org.paperplane.conference.repository.UserRepository;
import org.paperplane.conference.repository.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UsernameIsAlreadyInUseException(String.format("Username %s is already in use", signUpRequest.getUsername()));
        }

        var user = User.builder()
                .id(UUID.randomUUID().toString())
                .displayedName(signUpRequest.getDisplayedName())
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(getDefaultRoles())
                .build();
        userRepository.save(user);

        var accessToken = tokenService.createJwtToken(user);

        return SignUpResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public List<UserResponse> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public List<UserResponse> findUsersByDisplayedName(String displayedName) {
        return userRepository.findByDisplayedNameLike(displayedName)
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public User findUserEntityById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find user with id " + id));
    }

    private List<UserRole> getDefaultRoles() {
        var roles = new ArrayList<UserRole>();
        roles.add(userRoleRepository.findByName(Role.USER));
        return roles;
    }
}
