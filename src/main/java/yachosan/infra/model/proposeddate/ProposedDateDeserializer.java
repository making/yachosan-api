package yachosan.infra.model.proposeddate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import yachosan.domain.model.ProposedDate;

import java.io.IOException;

public class ProposedDateDeserializer extends StdScalarDeserializer<ProposedDate> {
    public ProposedDateDeserializer() {
        super(ProposedDate.class);
    }

    @Override
    public ProposedDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return ProposedDate.fromString(jp.getValueAsString());
    }
}
