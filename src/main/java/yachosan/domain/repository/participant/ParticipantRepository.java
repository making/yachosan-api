package yachosan.domain.repository.participant;

import org.springframework.data.jpa.repository.JpaRepository;
import yachosan.domain.model.ParticipantPk;
import yachosan.domain.model.YParticipant;

public interface ParticipantRepository extends JpaRepository<YParticipant, ParticipantPk> {
}
