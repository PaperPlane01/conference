package org.paperplane.conference.repository;

import org.paperplane.conference.model.Conference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConferenceRepository extends JpaRepository<Conference, String> {
    Optional<Conference> findByIdAndCanceledFalse(String id);
}
