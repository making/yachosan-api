package yachosan.api.participant;


import org.dozer.Mapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import yachosan.api.ResponseEntites;
import yachosan.domain.model.ParticipantPk;
import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YParticipant;
import yachosan.domain.repository.participant.ParticipantRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/schedules/{scheduleId}/participants")
public class ParticipantRestController {
    @Inject
    ParticipantRepository participantRepository;
    @Inject
    Mapper dozerMapper;

    @RequestMapping(method = RequestMethod.GET)
    List<YParticipant> getParticipants(@PathVariable("scheduleId") ScheduleId scheduleId) {
        return participantRepository.findByScheduleId(scheduleId);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<YParticipant> postParticipants(@PathVariable("scheduleId") ScheduleId scheduleId, @RequestBody YParticipant participant) {
        System.out.println(participant);
        participant.getParticipantPk().setScheduleId(scheduleId);
        YParticipant created = participantRepository.save(participant);
        return ResponseEntites.created(created);
    }

    @RequestMapping(value = "{nickname}", method = RequestMethod.GET)
    ResponseEntity<YParticipant> getParticipant(@PathVariable("scheduleId") ScheduleId scheduleId, @PathVariable("nickname") String nickname) {
        Optional<YParticipant> participant = participantRepository.findByParticipantPk(new ParticipantPk(scheduleId, nickname));
        return ResponseEntites.okIfPresent(participant);
    }


    @RequestMapping(value = "{nickname}", method = RequestMethod.PUT)
    ResponseEntity<YParticipant> putParticipant(@PathVariable("scheduleId") ScheduleId scheduleId, @PathVariable("nickname") String nickname,
                                                @Validated @RequestBody YParticipant update) {
        Optional<YParticipant> participant = participantRepository.findByParticipantPk(new ParticipantPk(scheduleId, nickname));
        return ResponseEntites.okIfPresent(participant.map(p -> {
            dozerMapper.map(update, p);
            System.out.println(p);
            return participantRepository.save(p);
        }));
    }
}
