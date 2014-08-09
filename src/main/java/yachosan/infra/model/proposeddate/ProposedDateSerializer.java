package yachosan.infra.model.proposeddate;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import yachosan.domain.model.ProposedDate;

import java.io.IOException;

public class ProposedDateSerializer extends StdScalarSerializer<ProposedDate> {

    public ProposedDateSerializer() {
        super(ProposedDate.class);
    }

    @Override
    public void serialize(ProposedDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeString(value.getStartDate().toString());
    }
}
