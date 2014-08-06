package yachosan.domain.repository.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YSchedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<YSchedule, ScheduleId> {
    Optional<YSchedule> findByScheduleId(ScheduleId scheduleId);

    @Query("SELECT DISTINCT x FROM YSchedule x JOIN FETCH x.participants JOIN x.proposedDates ORDER BY x.updatedAt DESC")
    List<YSchedule> findAllDetails();

    @Query("SELECT NEW yachosan.domain.repository.schedule.ScheduleSummary(x.scheduleId, x.scheduleName, x.scheduleDescription, x.createdAt, x.updatedAt) " +
            "FROM YSchedule x  ORDER BY x.updatedAt DESC")
    List<ScheduleSummary> findAllSummaries();
}
