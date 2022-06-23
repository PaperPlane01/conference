package org.paperplane.conference.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.paperplane.conference.config.DataInitializationConfigurationProperties;
import org.paperplane.conference.model.Role;
import org.paperplane.conference.model.User;
import org.paperplane.conference.model.UserRole;
import org.paperplane.conference.repository.UserRepository;
import org.paperplane.conference.repository.UserRoleRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InitialDataProvider {
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final DataInitializationConfigurationProperties dataInitializationProperties;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void runDataInitialization() {
        if (!dataInitializationProperties.isRunOnStart()) {
            return;
        }

        log.info("Creating initial data");

        createRoles();
        createUsers();
    }

    private void createRoles() {
        log.info("Creating user roles");

        Stream.of(Role.values()).forEach(role -> {
            if (userRoleRepository.existsByName(role)) {
                log.info("Role {} already exists", role);
            } else {
                log.info("Creating role {}", role);

                var userRole = UserRole.builder()
                        .id(UUID.randomUUID().toString())
                        .name(role)
                        .build();
                userRoleRepository.save(userRole);
            }
        });
    }

    private void createUsers() {
        log.info("Creating users");

        dataInitializationProperties.getUsers().forEach(initialUser -> {
            var roles = userRoleRepository.findByNameIn(initialUser.getRoles());
            var password = passwordEncoder.encode(initialUser.getPassword());

            var existingUserOptional = userRepository.findByUsername(initialUser.getUsername());

            if (existingUserOptional.isPresent()) {
                log.info("Updating user {}", initialUser.getUsername());

                var existingUser = existingUserOptional.get();
                existingUser.setRoles(roles);
                existingUser.setPassword(password);
                existingUser.setDisplayedName(initialUser.getDisplayedName());

                userRepository.save(existingUser);
            } else {
                log.info("Creating user {}", initialUser.getUsername());

                var user = User.builder()
                        .id(UUID.randomUUID().toString())
                        .displayedName(initialUser.getDisplayedName())
                        .username(initialUser.getUsername())
                        .password(password)
                        .roles(roles)
                        .build();
                userRepository.save(user);
            }
        });
    }
}
