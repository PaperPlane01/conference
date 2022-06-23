package org.paperplane.conference.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conference {
    @Id
    private String id;
    private String name;
    private int capacity;
    private ZonedDateTime createdAt;
    private boolean canceled;
    private ZonedDateTime canceledAt;

    @ManyToOne
    private User createdBy;

    @ManyToOne
    private User canceledBy;

    @OneToMany(mappedBy = "conference")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<ConferenceParticipant> participants;

    public boolean isCreatedBy(String userId) {
        return createdBy.getId().equals(userId);
    }
}
