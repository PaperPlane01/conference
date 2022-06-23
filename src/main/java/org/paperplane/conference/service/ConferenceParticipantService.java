package org.paperplane.conference.service;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.api.request.CreateConferenceParticipantRequest;
import org.paperplane.conference.api.response.ConferenceParticipantResponse;
import org.paperplane.conference.exception.ConferenceIsFullException;
import org.paperplane.conference.exception.NotFoundException;
import org.paperplane.conference.exception.UserIsAlreadyConferenceParticipantException;
import org.paperplane.conference.mapper.ConferenceParticipantMapper;
import org.paperplane.conference.model.ConferenceParticipant;
import org.paperplane.conference.repository.ConferenceParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ConferenceParticipantService {
    private final ConferenceParticipantRepository conferenceParticipantRepository;
    private final ConferenceService conferenceService;
    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final ConferenceParticipantMapper conferenceParticipantMapper;

    public ConferenceParticipantResponse createConferenceParticipant(String conferenceId, CreateConferenceParticipantRequest createConferenceParticipantRequest) {
        if (conferenceParticipantRepository.existsByConferenceIdAndUserId(conferenceId, createConferenceParticipantRequest.getUserId())) {
            throw new UserIsAlreadyConferenceParticipantException(
                    String.format("User %s is already participant of conference %s", createConferenceParticipantRequest.getUserId(), conferenceId)
            );
        }

        var currentUser = authorizationService.requireCurrentUser();
        var conference = conferenceService.getConferenceEntityById(conferenceId);

        if (conference.getParticipants().size() == conference.getCapacity()) {
            throw new ConferenceIsFullException("Conference " + conferenceId + " has reached max capacity");
        }

        var user = userService.findUserEntityById(createConferenceParticipantRequest.getUserId());

        var conferenceParticipant = ConferenceParticipant.builder()
                .id(UUID.randomUUID().toString())
                .conference(conference)
                .user(user)
                .name(
                        StringUtils.hasText(createConferenceParticipantRequest.getName())
                                ? createConferenceParticipantRequest.getName()
                                : user.getDisplayedName()
                )
                .addedBy(currentUser)
                .build();
        conferenceParticipantRepository.save(conferenceParticipant);

        return conferenceParticipantMapper.toConferenceParticipantResponse(conferenceParticipant);
    }

    public void deleteConferenceParticipant(String id, String conferenceId) {
        var conferenceParticipant = conferenceParticipantRepository
                .findByIdAndConferenceId(id, conferenceId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Could not find participant %s in conference %s", id, conferenceId)
                ));
        conferenceParticipantRepository.delete(conferenceParticipant);
    }

    public List<ConferenceParticipantResponse> findAllConferenceParticipants(String conferenceId) {
        return conferenceService
                .getConferenceEntityById(conferenceId)
                .getParticipants()
                .stream()
                .map(conferenceParticipantMapper::toConferenceParticipantResponse)
                .toList();
    }
}
