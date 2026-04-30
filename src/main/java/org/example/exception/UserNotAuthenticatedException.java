package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when user authentication fails or user is not found
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotAuthenticatedException extends RuntimeException {

    public UserNotAuthenticatedException(String message) {
        super(message);
    }

    public UserNotAuthenticatedException() {
        super("User not authenticated");
    }
}
