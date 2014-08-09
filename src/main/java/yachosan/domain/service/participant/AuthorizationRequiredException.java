package yachosan.domain.service.participant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationRequiredException extends RuntimeException {
    public AuthorizationRequiredException() {
        super("authorization required");
    }
}
