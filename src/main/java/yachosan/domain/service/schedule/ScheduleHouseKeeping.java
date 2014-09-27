package yachosan.domain.service.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import yachosan.domain.model.YSchedule;
import yachosan.domain.repository.schedule.ScheduleRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ScheduleHouseKeeping {
    @Autowired
    ScheduleRepository scheduleRepository;

    @Value("${yachosan.clean.interval.day:3}")
    long cleanIntervalDay;

    private static final Logger logger = LoggerFactory.getLogger(ScheduleHouseKeeping.class);

    @Scheduled(fixedRate = 1_000 * 60 * 60 * 1 /* 1h */, initialDelay = 3_000)
    public void cleanSchedule() {
        LocalDateTime cleanDateTime = LocalDateTime.now().minusDays(cleanIntervalDay);
        List<YSchedule> target = scheduleRepository.findAllNoParticipantsBeforeCleanDateTime(cleanDateTime);
        scheduleRepository.delete(target);
        if (logger.isInfoEnabled()) {
            logger.info("Delete schedules {}", target.stream().map(YSchedule::getScheduleId));
        }
    }
}
