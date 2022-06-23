package org.paperplane.conference.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateConferenceRequest {
    @NotBlank
    private String name;

    @Size(max = 50)
    private List<String> usersIds;

    @Min(1)
    @Max(50)
    private Integer capacity;
}
