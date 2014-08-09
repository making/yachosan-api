package yachosan.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Password implements Serializable {
    public static String MASKED_STRING = "****";
    public static Password MASKED = Password.of(MASKED_STRING.toCharArray());

    @Size(max = 255)
    @Column(name = "password")
    private char[] value;

    public String toString() {
        return value == null ? "" : MASKED_STRING;
    }

    @NoArgsConstructor
    public static class UnmaskedPassword extends Password {
        public UnmaskedPassword(String password) {
            // for test
            super();
            setValue(password.toCharArray());
        }
    }
}
