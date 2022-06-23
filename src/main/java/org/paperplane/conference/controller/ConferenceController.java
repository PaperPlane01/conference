package org.paperplane.conference.controller;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.api.request.ConferenceSeatsAvailabilityResponse;
import org.paperplane.conference.api.request.CreateConferenceRequest;
import org.paperplane.conference.api.response.ConferenceResponse;
import org.paperplane.conference.service.ConferenceService;
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

@RestController
@RequestMapping("/api/v1/conferences")
@RequiredArgsConstructor
public class ConferenceController {
    private final ConferenceService conferenceService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConferenceResponse createConference(@RequestBody @Valid CreateConferenceRequest createConferenceRequest) {
        return conferenceService.createConference(createConferenceRequest);
    }

    @GetMapping("/{id}")
    public ConferenceResponse getConferenceById(@PathVariable String id) {
        return conferenceService.getConferenceById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @conferencePermissions.canCancelConference(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelConference(@PathVariable String id) {
        conferenceService.cancelConference(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/has-seats")
    public ConferenceSeatsAvailabilityResponse checkConferenceSeatsAvailability(@PathVariable String id) {
        return conferenceService.checkConferenceSeatsAvailability(id);
    }
}
