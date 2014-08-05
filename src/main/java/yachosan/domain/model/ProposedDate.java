package yachosan.domain.model;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(using = ProposedDate.ProposedDateSerializer.class)
public class ProposedDate implements Serializable {
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @NotNull
    private LocalDate startDate;

    @Override
    public String toString() {
        return startDate.toString();
    }


    public static class ProposedDateSerializer extends StdScalarSerializer<ProposedDate> {

        public ProposedDateSerializer() {
            super(ProposedDate.class);
        }

        @Override
        public void serialize(ProposedDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeString(value.getStartDate().toString());
        }
    }
}