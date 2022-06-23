package org.paperplane.conference.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConferenceResponse {
    private String id;
    private String name;
    private int participantsCount;
    private int capacity;
    private ZonedDateTime createdAt;
    private UserResponse createdBy;
}
