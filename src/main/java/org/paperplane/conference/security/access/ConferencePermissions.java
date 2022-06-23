package org.paperplane.conference.security.access;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.service.AuthorizationService;
import org.paperplane.conference.service.ConferenceService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConferencePermissions {
    private final ConferenceService conferenceService;
    private final AuthorizationService authorizationService;

    public boolean canCancelConference(String id) {
        return conferenceService
                .getConferenceEntityById(id)
                .isCreatedBy(authorizationService.requireCurrentUserDetails().getId());
    }
}
