package org.paperplane.conference.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.paperplane.conference.api.response.ConferenceResponse;
import org.paperplane.conference.model.Conference;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public abstract class ConferenceMapper {

    @BeanMapping(resultType = ConferenceResponse.class)
    public abstract ConferenceResponse toConferenceResponse(Conference conference);

    @AfterMapping
    protected void afterMapping(Conference conference, @MappingTarget ConferenceResponse.ConferenceResponseBuilder conferenceResponse) {
        conferenceResponse.participantsCount(conference.getParticipants().size());
    }
}
