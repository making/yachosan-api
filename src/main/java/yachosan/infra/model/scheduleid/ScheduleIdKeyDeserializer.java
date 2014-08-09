package yachosan.infra.model.scheduleid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import yachosan.domain.model.ScheduleId;

import java.io.IOException;

public class ScheduleIdKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return ScheduleId.of(key);
    }
}
