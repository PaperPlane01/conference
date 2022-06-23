package org.paperplane.conference.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ParticipantsCountExceedsCapacityException extends RuntimeException {
    public ParticipantsCountExceedsCapacityException() {
    }

    public ParticipantsCountExceedsCapacityException(String message) {
        super(message);
    }

    public ParticipantsCountExceedsCapacityException(String message, Throwable cause) {
        super(message, cause);
    }
}
