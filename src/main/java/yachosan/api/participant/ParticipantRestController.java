package yachosan.api.participant;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YParticipant;
import yachosan.domain.repository.schedule.ScheduleRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/schedules/{scheduleId}/participants")
public class ParticipantRestController {
    @Inject
    ScheduleRepository scheduleRepository;

    @RequestMapping(method = RequestMethod.GET)
    List<YParticipant> getParticipants(@PathVariable("scheduleId") ScheduleId scheduleId) {
        return scheduleRepository.findOne(scheduleId).getParticipants();
    }

    @RequestMapping(value = "{nickname}", method = RequestMethod.GET)
    YParticipant getParticipant(@PathVariable("scheduleId") ScheduleId scheduleId, @PathVariable("nickname") String nickname) {
        YParticipant participant = scheduleRepository.findOne(scheduleId).getParticipants().stream()
                .filter(p -> Objects.equals(p.getParticipantPk().getNickname(), nickname))
                .findFirst().get();
        return participant;
    }
}
