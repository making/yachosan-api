package yachosan.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.nio.CharBuffer;
import java.util.Optional;

@Embeddable
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Password implements Serializable {
    public static final String MASKED_STRING = "****";
    public static final Password MASKED = Password.of(MASKED_STRING.toCharArray());
    public static final Password NULL = Password.of(null);
    public static final PasswordEncoder DEFAULT_PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Size(max = 255)
    @Column(name = "password")
    private char[] value;

    public String toString() {
        return value == null ? "null" : MASKED_STRING;
    }

    public Optional<char[]> optional() {
        return Optional.ofNullable(value);
    }

    public Optional<EncodedPassword> encode() {
        return encode(DEFAULT_PASSWORD_ENCODER);
    }

    public Optional<EncodedPassword> encode(PasswordEncoder passwordEncoder) {
        return optional().map(raw -> new EncodedPassword(passwordEncoder.encode(CharBuffer.wrap(raw))));
    }

    public EncodedPassword alreadyEncoded() {
        return new EncodedPassword(String.valueOf(this.value));
    }

    @NoArgsConstructor
    public static class UnmaskedPassword extends Password {
        public UnmaskedPassword(String password) {
            // for test
            super();
            setValue(password == null ? null : password.toCharArray());
        }
    }

    public static class EncodedPassword extends Password {
        private final String encoded;

        public EncodedPassword(String encoded) {
            super(encoded.toCharArray());
            this.encoded = encoded;
        }

        public boolean matches(PasswordEncoder encoder, Optional<Password> rawPassword) {
            return rawPassword
                    .map(p -> encoder.matches(CharBuffer.wrap(p.getValue()), encoded))
                    .orElse(Boolean.FALSE);
        }

        public boolean matches(Optional<Password> rawPassword) {
            return matches(DEFAULT_PASSWORD_ENCODER, rawPassword);
        }

        @Override
        public EncodedPassword alreadyEncoded() {
            return this;
        }
    }
}
