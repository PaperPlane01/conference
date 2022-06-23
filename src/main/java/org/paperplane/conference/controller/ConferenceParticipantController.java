package org.paperplane.conference.controller;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.api.request.CreateConferenceParticipantRequest;
import org.paperplane.conference.api.response.ConferenceParticipantResponse;
import org.paperplane.conference.service.ConferenceParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/conferences")
@RequiredArgsConstructor
public class ConferenceParticipantController {
    private final ConferenceParticipantService conferenceParticipantService;

    @PreAuthorize("hasRole('ADMIN') or @conferenceParticipantPermissions.canAddParticipantToConference(#conferenceId)")
    @PostMapping("/{conferenceId}/participants")
    @ResponseStatus(HttpStatus.CREATED)
    public ConferenceParticipantResponse createConferenceParticipant(@PathVariable String conferenceId,
                                                                     @RequestBody @Valid CreateConferenceParticipantRequest createConferenceParticipantRequest) {
        System.out.println("controller");
        return conferenceParticipantService.createConferenceParticipant(conferenceId, createConferenceParticipantRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @conferenceParticipantPermissions.canRemoveParticipantFromConference('#conferenceId')")
    @DeleteMapping("/{conferenceId}/participants/{participantId}")
    public ResponseEntity<?> removeConferenceParticipant(@PathVariable String conferenceId, @PathVariable String participantId) {
        conferenceParticipantService.deleteConferenceParticipant(participantId, conferenceId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{conferenceId}/participants")
    public List<ConferenceParticipantResponse> findAllConferenceParticipants(@PathVariable String conferenceId) {
        return conferenceParticipantService.findAllConferenceParticipants(conferenceId);
    }
}
