package yachosan.infra.model.password;

import org.dozer.DozerConverter;
import yachosan.domain.model.Password;

public class PasswordDozerConverter extends DozerConverter<Password, Password> {
    public PasswordDozerConverter() {
        super(Password.class, Password.class);
    }

    @Override
    public Password convertTo(Password password, Password password2) {
        return Password.of(password.getValue());
    }

    @Override
    public Password convertFrom(Password password, Password password2) {
        return Password.of(password.getValue());
    }
}
