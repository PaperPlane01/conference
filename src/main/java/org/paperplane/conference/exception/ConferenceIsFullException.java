package org.paperplane.conference.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class ConferenceIsFullException extends RuntimeException {
    public ConferenceIsFullException() {
    }

    public ConferenceIsFullException(String message) {
        super(message);
    }

    public ConferenceIsFullException(String message, Throwable cause) {
        super(message, cause);
    }
}
