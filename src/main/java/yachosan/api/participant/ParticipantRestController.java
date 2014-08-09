package yachosan.api.participant;


import org.dozer.Mapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import yachosan.api.ResponseEntites;
import yachosan.domain.model.Password;
import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YParticipant;
import yachosan.domain.service.participant.ParticipantService;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/schedules/{scheduleId}/participants")
public class ParticipantRestController {
    @Inject
    ParticipantService participantService;
    @Inject
    Mapper dozerMapper;

    @RequestMapping(method = RequestMethod.GET)
    List<YParticipant> getParticipants(@PathVariable("scheduleId") ScheduleId scheduleId) {
        return participantService.findByScheduleId(scheduleId);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<YParticipant> postParticipants(@PathVariable("scheduleId") ScheduleId scheduleId, @RequestBody YParticipant participant) {
        participant.getParticipantPk().setScheduleId(scheduleId);
        YParticipant created = participantService.create(participant, Optional.ofNullable(participant.getPassword()));
        return ResponseEntites.created(created);
    }

    @RequestMapping(value = "{nickname}", method = RequestMethod.GET)
    ResponseEntity<YParticipant> getParticipant(@PathVariable("scheduleId") ScheduleId scheduleId, @PathVariable("nickname") String nickname) {
        Optional<YParticipant> participant = participantService.findOne(scheduleId, nickname);
        return ResponseEntites.okIfPresent(participant);
    }

    @RequestMapping(value = "{nickname}", method = RequestMethod.PUT)
    ResponseEntity<YParticipant> putParticipant(@PathVariable("scheduleId") ScheduleId scheduleId, @PathVariable("nickname") String nickname,
                                                @Validated @RequestBody YParticipant update,
                                                Password password) {
        Optional<YParticipant> participant = participantService.findOne(scheduleId, nickname);
        return ResponseEntites.okIfPresent(participant.map(p -> {
            Optional<Password> originalEncodedPassword = p.getPasswordOptional();
            dozerMapper.map(update, p);
            return participantService.update(p,
                    p.getPasswordOptional(),
                    originalEncodedPassword,
                    Optional.ofNullable(password));
        }));
    }

    @RequestMapping(value = "{nickname}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteParticipant(@PathVariable("scheduleId") ScheduleId scheduleId, @PathVariable("nickname") String nickname,
                                           Password password) {
        Optional<YParticipant> participant = participantService.findOne(scheduleId, nickname);
        return ResponseEntites.noContentIfPresent(participant.map((p -> {
            participantService.delete(p, Optional.ofNullable(password));
            return p;
        })));
    }
}
