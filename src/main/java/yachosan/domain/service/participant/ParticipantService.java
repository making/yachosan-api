package yachosan.domain.service.participant;

import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Inject
    PasswordEncoder passwordEncoder;


    public List<YParticipant> findByScheduleId(ScheduleId scheduleId) {
        return participantRepository.findByScheduleId(scheduleId).stream()
                .map(YParticipant::alreadyPasswordEncoded)
                .collect(Collectors.toList());
    }

    public Optional<YParticipant> findOne(ScheduleId scheduleId, String nickname) {
        return participantRepository.findByParticipantPk(new ParticipantPk(scheduleId, nickname))
                .map(YParticipant::alreadyPasswordEncoded);
    }

    public YParticipant create(YParticipant participant, Optional<Password> rawPassword) {
        rawPassword
                .flatMap(p -> p.encode(passwordEncoder))
                .ifPresent(participant::setPassword);
        return participantRepository.save(participant);
    }

    void authorize(YParticipant participant, Optional<Password> originalRawPassword) {
        participant.getPasswordOptional()
                .map(Password::alreadyEncoded)
                .map(p -> {
                    if (!originalRawPassword.isPresent()) {
                        throw new AuthorizationRequiredException();
                    }
                    return p;
                })
                .map(p -> p.matches(passwordEncoder, originalRawPassword))
                .map(matches -> {
                    if (!matches) {
                        throw new AuthorizationFailedException();
                    }
                    return true;
                });
    }

    public YParticipant update(YParticipant participant, Optional<Password> newRawPassword, Optional<Password> originalRawPassword) {
        authorize(participant, originalRawPassword);
        newRawPassword
                .flatMap(p -> p.encode(passwordEncoder))
                .ifPresent(participant::setPassword);
        return participantRepository.save(participant);
    }

    public void delete(YParticipant participant, Optional<Password> originalRawPassword) {
        authorize(participant, originalRawPassword);
        participantRepository.delete(participant);
    }

}
