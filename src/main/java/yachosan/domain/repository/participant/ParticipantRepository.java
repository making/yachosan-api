package yachosan.domain.repository.participant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yachosan.domain.model.ParticipantPk;
import yachosan.domain.model.ScheduleId;
import yachosan.domain.model.YParticipant;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<YParticipant, ParticipantPk> {
    @Query("SELECT DISTINCT x FROM YParticipant x JOIN FETCH x.replies JOIN FETCH x.schedule WHERE x.participantPk.scheduleId = :scheduleId ORDER BY x.participantPk.nickname ASC")
    List<YParticipant> findByScheduleId(@Param("scheduleId") ScheduleId scheduleId);

    Optional<YParticipant> findByParticipantPk(ParticipantPk participantPk);
}
