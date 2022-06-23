package org.paperplane.conference.repository;

import org.paperplane.conference.model.ConferenceParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConferenceParticipantRepository extends JpaRepository<ConferenceParticipant, String> {
    boolean existsByConferenceIdAndUserId(String conferenceId, String userId);
    Optional<ConferenceParticipant> findByIdAndConferenceId(String id, String conferenceId);
}
