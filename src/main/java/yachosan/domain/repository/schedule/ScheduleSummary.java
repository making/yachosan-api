package yachosan.domain.repository.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yachosan.domain.model.ScheduleId;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleSummary {
    private ScheduleId scheduleId;
    private String scheduleName;
    private String scheduleDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
