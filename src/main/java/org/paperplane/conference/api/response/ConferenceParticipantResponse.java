package org.paperplane.conference.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConferenceParticipantResponse {
    private String id;
    private String name;
    private UserResponse user;
}
