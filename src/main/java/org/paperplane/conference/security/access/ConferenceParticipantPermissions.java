package org.paperplane.conference.security.access;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.service.AuthorizationService;
import org.paperplane.conference.service.ConferenceService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConferenceParticipantPermissions {
    private final ConferenceService conferenceService;
    private final AuthorizationService authorizationService;

    public boolean canAddParticipantToConference(String conferenceId) {
        return conferenceService
                .getConferenceEntityById(conferenceId)
                .isCreatedBy(authorizationService.requireCurrentUserDetails().getId());
    }

    public boolean canRemoveParticipantFromConference(String conferenceId) {
        return conferenceService
                .getConferenceEntityById(conferenceId)
                .isCreatedBy(authorizationService.requireCurrentUserDetails().getId());
    }
}
