package yachosan.api.schedule;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import yachosan.api.ResponseEntites;
import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YSchedule;
import yachosan.domain.repository.schedule.ScheduleSummary;
import yachosan.domain.service.schedule.ScheduleService;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/schedules")
public class ScheduleRestController {
    @Inject
    ScheduleService scheduleService;


    @RequestMapping(method = RequestMethod.GET)
    List<ScheduleSummary> getSchedules() {
        return scheduleService.findAllSummaries();
    }

    @RequestMapping(value = "{scheduleId}", method = RequestMethod.GET)
    ResponseEntity<YSchedule> getSchedule(@PathVariable("scheduleId") ScheduleId scheduleId) {
        Optional<YSchedule> schedule = scheduleService.findOne(scheduleId);
        return ResponseEntites.okIfPresent(schedule);
    }
}
