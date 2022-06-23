package org.paperplane.conference.mapper;

import org.mapstruct.Mapper;
import org.paperplane.conference.api.response.ConferenceParticipantResponse;
import org.paperplane.conference.model.ConferenceParticipant;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ConferenceParticipantMapper {

    ConferenceParticipantResponse toConferenceParticipantResponse(ConferenceParticipant conferenceParticipant);
}
