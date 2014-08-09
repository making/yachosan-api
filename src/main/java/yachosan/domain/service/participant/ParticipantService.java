package yachosan.domain.service.participant;

import yachosan.domain.model.ParticipantPk;
import yachosan.domain.model.Password;
import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YParticipant;
import yachosan.domain.repository.participant.ParticipantRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Named
@Transactional
public class ParticipantService {
    @Inject
    ParticipantRepository participantRepository;

    public List<YParticipant> findByScheduleId(ScheduleId scheduleId) {
        return participantRepository.findByScheduleId(scheduleId).stream()
                .map(p -> p.alreadyPasswordEncoded())
                .collect(Collectors.toList());
    }

    public Optional<YParticipant> findOne(ScheduleId scheduleId, String nickname) {
        return participantRepository.findByParticipantPk(new ParticipantPk(scheduleId, nickname))
                .map(p -> p.alreadyPasswordEncoded());
    }

    public YParticipant create(YParticipant participant, Optional<Password> rawPassword) {
        rawPassword
                .flatMap(p -> p.encode())
                .ifPresent(participant::setPassword);
        return participantRepository.save(participant);
    }

    void authorize(Optional<Password> originalEncodedPassword, Optional<Password> originalRawPassword) {
        originalEncodedPassword
                .map(p -> p.alreadyEncoded())
                .map(p -> {
                    if (!originalRawPassword.isPresent()) {
                        throw new AuthorizationRequiredException();
                    }
                    return p;
                })
                .map(p -> p.matches(originalRawPassword))
                .ifPresent(matches -> {
                    if (!matches) {
                        throw new AuthorizationFailedException();
                    }
                });
    }

    public YParticipant update(YParticipant participant,
                               Optional<Password> newRawPassword,
                               Optional<Password> originalEncodedPassword,
                               Optional<Password> confirmRawPassword) {
        authorize(originalEncodedPassword, confirmRawPassword);
        newRawPassword
                .flatMap(p -> p.encode())
                .ifPresent(participant::setPassword);
        return participantRepository.save(participant);
    }

    public void delete(YParticipant participant, Optional<Password> confirmRawPassword) {
        authorize(participant.getPasswordOptional(), confirmRawPassword);
        participantRepository.delete(participant);
    }

}
