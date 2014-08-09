package yachosan.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ScheduleId implements Serializable {
    @Size(min = 36, max = 36)
    @NotNull
    @Column(length = 36, name = "schedule_id")
    private String value;

    @Override
    public String toString() {
        return value;
    }
}
