package yachosan.infra.model.scheduleid;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import yachosan.domain.model.ScheduleId;

import java.io.IOException;

public class ScheduleIdDeserializer extends StdScalarDeserializer<ScheduleId> {

    public ScheduleIdDeserializer() {
        super(ScheduleId.class);
    }

    @Override
    public ScheduleId deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return ScheduleId.of(jp.getValueAsString());
    }
}
