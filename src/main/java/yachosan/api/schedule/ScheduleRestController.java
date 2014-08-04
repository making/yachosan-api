package yachosan.api.schedule;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YSchedule;
import yachosan.domain.repository.schedule.ScheduleRepository;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("api/v1/schedules")
public class ScheduleRestController {
    @Inject
    ScheduleRepository scheduleRepository;


    @RequestMapping(method = RequestMethod.GET)
    List<YSchedule> getSchedules() {
        return scheduleRepository.findAllDetails();
    }

    @RequestMapping(value = "{scheduleId}", method = RequestMethod.GET)
    YSchedule getSchedule(@PathVariable("scheduleId") String scheduleId) {
        YSchedule schedule = scheduleRepository.findOne(ScheduleId.of(scheduleId));
        return schedule;
    }
}
