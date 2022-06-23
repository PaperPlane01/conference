package org.paperplane.conference.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameIsAlreadyInUseException extends RuntimeException {
    public UsernameIsAlreadyInUseException() {
    }

    public UsernameIsAlreadyInUseException(String message) {
        super(message);
    }

    public UsernameIsAlreadyInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
