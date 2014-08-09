package yachosan.infra.model.proposeddate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import yachosan.domain.model.ProposedDate;

import java.io.IOException;

public class ProposedDateKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return ProposedDate.fromString(key);
    }
}
