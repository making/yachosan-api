package yachosan.domain.repository.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YSchedule;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<YSchedule, ScheduleId> {
    @Query("SELECT x FROM YSchedule x JOIN FETCH x.participants JOIN x.proposedDates ORDER BY x.updatedAt DESC")
    List<YSchedule> findAllDetails();
}
