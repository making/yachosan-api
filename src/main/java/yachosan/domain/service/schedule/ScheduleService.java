package yachosan.domain.service.schedule;

import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YSchedule;
import yachosan.domain.repository.schedule.ScheduleRepository;
import yachosan.domain.repository.schedule.ScheduleSummary;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Named
@Transactional
public class ScheduleService {
    @Inject
    ScheduleRepository scheduleRepository;

    public List<ScheduleSummary> findAllSummaries() {
        return scheduleRepository.findAllSummaries();
    }

    public Optional<YSchedule> findOne(ScheduleId scheduleId) {
        return scheduleRepository.findByScheduleId(scheduleId);
    }

    public YSchedule create(YSchedule schedule) {
        LocalDateTime now = LocalDateTime.now();
        schedule.setCreatedAt(now);
        schedule.setUpdatedAt(now);
        return scheduleRepository.save(schedule);
    }

    public YSchedule update(YSchedule schedule) {
        schedule.setUpdatedAt(LocalDateTime.now());
        return scheduleRepository.save(schedule);
    }
}
