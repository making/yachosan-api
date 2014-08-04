package yachosan.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantPk implements Serializable {
    @Embedded
    private ScheduleId scheduleId;
    @NotNull
    @Size(min = 1, max = 50)
    private String nickname;
}
