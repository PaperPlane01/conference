package org.paperplane.conference.repository;

import org.paperplane.conference.model.Role;
import org.paperplane.conference.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    UserRole findByName(Role name);
    List<UserRole> findByNameIn(List<Role> names);
    boolean existsByName(Role name);
}
