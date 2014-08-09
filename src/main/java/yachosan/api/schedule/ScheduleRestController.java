package yachosan.api.schedule;

import org.dozer.Mapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import yachosan.api.ResponseEntites;
import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YSchedule;
import yachosan.domain.repository.schedule.ScheduleSummary;
import yachosan.domain.service.schedule.ScheduleService;

import javax.inject.Inject;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/schedules")
public class ScheduleRestController {
    @Inject
    ScheduleService scheduleService;
    @Inject
    Mapper beanMapper;

    @RequestMapping(method = RequestMethod.GET)
    List<ScheduleSummary> getSchedules() {
        return scheduleService.findAllSummaries();
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<YSchedule> postSchedules(@Validated @RequestBody YSchedule schedule, UriComponentsBuilder uriBuilder) {
        YSchedule created = scheduleService.create(schedule);
        URI location = uriBuilder.path("api/v1/schedules/{scheduleId}")
                .buildAndExpand(schedule.getScheduleId().getValue()).toUri();
        return ResponseEntites.created(created, location);
    }

    @RequestMapping(value = "{scheduleId}", method = RequestMethod.GET)
    ResponseEntity<YSchedule> getSchedule(@PathVariable("scheduleId") ScheduleId scheduleId) {
        Optional<YSchedule> schedule = scheduleService.findOne(scheduleId);
        return ResponseEntites.okIfPresent(schedule);
    }

    @RequestMapping(value = "{scheduleId}", method = RequestMethod.PUT)
    ResponseEntity<YSchedule> putSchedule(@PathVariable("scheduleId") ScheduleId scheduleId, @Validated @RequestBody YSchedule update) {
        Optional<YSchedule> schedule = scheduleService.findOne(scheduleId);
        return ResponseEntites.okIfPresent(schedule.map(s -> {
            beanMapper.map(update, s);
            return scheduleService.update(s);
        }));
    }
}
