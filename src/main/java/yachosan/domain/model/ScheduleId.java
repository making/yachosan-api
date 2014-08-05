package yachosan.domain.model;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@JsonSerialize(using = ScheduleId.ScheduleIdSerializer.class)
public class ScheduleId implements Serializable {
    @Size(min = 36, max = 36)
    @NotNull
    @Column(length = 36, name = "schedule_id")
    private String value;

    @Override
    public String toString() {
        return value;
    }


    public static class ScheduleIdSerializer extends StdScalarSerializer<ScheduleId> {

        public ScheduleIdSerializer() {
            super(ScheduleId.class);
        }

        @Override
        public void serialize(ScheduleId value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeString(value.getValue());
        }
    }
}
