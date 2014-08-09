package yachosan.infra.scheduleid;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import yachosan.domain.model.ScheduleId;

import java.io.IOException;

public class ScheduleIdSerializer extends StdScalarSerializer<ScheduleId> {

    public ScheduleIdSerializer() {
        super(ScheduleId.class);
    }

    @Override
    public void serialize(ScheduleId value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeString(value.getValue());
    }
}
