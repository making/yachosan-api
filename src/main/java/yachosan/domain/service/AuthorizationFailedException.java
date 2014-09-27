package yachosan.domain.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthorizationFailedException extends RuntimeException {
    public AuthorizationFailedException() {
        super("authorization failed");
    }
}
