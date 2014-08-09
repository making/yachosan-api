package yachosan.infra.model.password;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import yachosan.domain.model.Password;

import java.io.IOException;

public class PasswordSerializer extends StdScalarSerializer<Password> {
    public PasswordSerializer() {
        super(Password.class);
    }

    @Override
    public void serialize(Password value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        if (value instanceof Password.UnmaskedPassword) {
            jgen.writeString(new String(value.getValue()));
        } else {
            // hide password
            jgen.writeString(Password.MASKED_STRING);
        }
    }
}
