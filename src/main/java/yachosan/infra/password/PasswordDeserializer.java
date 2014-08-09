package yachosan.infra.password;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import yachosan.domain.model.Password;

import java.io.IOException;

public class PasswordDeserializer extends StdScalarDeserializer<Password> {

    public PasswordDeserializer() {
        super(Password.class);
    }

    @Override
    public Password deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String str = jp.getValueAsString();
        return (str == null) ? null : Password.of(str.toCharArray());
    }
}
