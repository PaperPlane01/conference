package org.paperplane.conference.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserIsAlreadyConferenceParticipantException extends RuntimeException {
    public UserIsAlreadyConferenceParticipantException() {
    }

    public UserIsAlreadyConferenceParticipantException(String message) {
        super(message);
    }

    public UserIsAlreadyConferenceParticipantException(String message, Throwable cause) {
        super(message, cause);
    }
}
