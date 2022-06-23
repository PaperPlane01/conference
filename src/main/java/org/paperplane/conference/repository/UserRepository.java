package org.paperplane.conference.repository;

import org.paperplane.conference.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByDisplayedNameLike(String displayedName);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
