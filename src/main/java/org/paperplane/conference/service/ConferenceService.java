package org.paperplane.conference.service;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.api.request.ConferenceSeatsAvailabilityResponse;
import org.paperplane.conference.api.request.CreateConferenceRequest;
import org.paperplane.conference.api.response.ConferenceResponse;
import org.paperplane.conference.exception.NotFoundException;
import org.paperplane.conference.exception.ParticipantsCountExceedsCapacityException;
import org.paperplane.conference.mapper.ConferenceMapper;
import org.paperplane.conference.model.Conference;
import org.paperplane.conference.model.ConferenceParticipant;
import org.paperplane.conference.model.User;
import org.paperplane.conference.repository.ConferenceParticipantRepository;
import org.paperplane.conference.repository.ConferenceRepository;
import org.paperplane.conference.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ConferenceService {
    private final ConferenceRepository conferenceRepository;
    private final ConferenceParticipantRepository conferenceParticipantRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;
    private final ConferenceMapper conferenceMapper;

    public ConferenceResponse createConference(CreateConferenceRequest createConferenceRequest) {
        var usersIds = createConferenceRequest.getUsersIds();
        var currentUser = authorizationService.requireCurrentUser();

        if (!createConferenceRequest.getUsersIds().contains(currentUser.getId())) {
            usersIds.add(currentUser.getId());
        }

        if (usersIds.size() > createConferenceRequest.getCapacity()) {
            throw new ParticipantsCountExceedsCapacityException("Number of initial users cannot exceed capacity");
        }

        var users = !createConferenceRequest.getUsersIds().isEmpty()
                ? userRepository.findAllById(createConferenceRequest.getUsersIds())
                : new ArrayList<User>();
        var conference = Conference.builder()
                .id(UUID.randomUUID().toString())
                .name(createConferenceRequest.getName())
                .createdAt(ZonedDateTime.now())
                .capacity(createConferenceRequest.getCapacity())
                .createdBy(authorizationService.requireCurrentUser())
                .build();
        var conferenceParticipants = users
                .stream()
                .map(user -> ConferenceParticipant.builder()
                        .id(UUID.randomUUID().toString())
                        .conference(conference)
                        .user(user)
                        .name(user.getDisplayedName())
                        .build()
                )
                        .toList();
        conferenceRepository.save(conference);
        conferenceParticipantRepository.saveAll(conferenceParticipants);

        conference.setParticipants(conferenceParticipants);

        return conferenceMapper.toConferenceResponse(conference);
    }

    public ConferenceResponse getConferenceById(String id) {
        return conferenceMapper.toConferenceResponse(getConferenceEntityById(id));
    }

    public void cancelConference(String id) {
        var currentUser = authorizationService.requireCurrentUser();
        var conference = getConferenceEntityById(id);

        conference.setCanceled(true);
        conference.setCanceledAt(ZonedDateTime.now());
        conference.setCanceledBy(currentUser);

        conferenceRepository.save(conference);
    }

    public ConferenceSeatsAvailabilityResponse checkConferenceSeatsAvailability(String id) {
        var conference = getConferenceEntityById(id);

        return ConferenceSeatsAvailabilityResponse.builder()
                .available(conference.getCapacity() > conference.getParticipants().size())
                .build();
    }

    public Conference getConferenceEntityById(String id) {
        return conferenceRepository.findByIdAndCanceledFalse(id).orElseThrow(() -> new NotFoundException("Cannot find conference with id " + id));
    }
}
