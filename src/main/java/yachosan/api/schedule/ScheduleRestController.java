package yachosan.api.schedule;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import yachosan.api.ResponseEntites;
import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YSchedule;
import yachosan.domain.repository.schedule.ScheduleRepository;
import yachosan.domain.repository.schedule.ScheduleSummary;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/schedules")
public class ScheduleRestController {
    @Inject
    ScheduleRepository scheduleRepository;


    @RequestMapping(method = RequestMethod.GET)
    List<ScheduleSummary> getSchedules() {
        return scheduleRepository.findAllSummaries();
    }

    @RequestMapping(value = "{scheduleId}", method = RequestMethod.GET)
    ResponseEntity<YSchedule> getSchedule(@PathVariable("scheduleId") ScheduleId scheduleId) {
        Optional<YSchedule> schedule = scheduleRepository.findByScheduleId(scheduleId);
        return ResponseEntites.okIfPresent(schedule);
    }
}
