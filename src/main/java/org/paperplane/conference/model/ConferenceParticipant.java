package org.paperplane.conference.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConferenceParticipant {
    @Id
    private String id;

    private String name;

    @ManyToOne
    private Conference conference;

    @ManyToOne
    private User user;

    @ManyToOne
    private User addedBy;
}
