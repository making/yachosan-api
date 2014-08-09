package yachosan.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "y_schedule",
        indexes = {
                @Index(columnList = "updatedAt")
        })
public class YSchedule implements Serializable {
    /**
     * スケジュールID
     */
    @EmbeddedId
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "yachosan.infra.model.scheduleid.ScheduleIdGenerator")
    private ScheduleId scheduleId;

    /**
     * スケジュール名
     */
    @NotNull
    @Size(min = 1, max = 50)
    private String scheduleName;

    /**
     * 説明
     */
    @NotNull
    @Size(min = 1, max = 200)
    private String scheduleDescription;
    /**
     * 候補日リスト
     */
    @ElementCollection
    @CollectionTable(name = "y_schedule_proposed_dates",
            joinColumns = @JoinColumn(name = "schedule_id"),
            indexes = @Index(columnList = "startDate"))
    private List<ProposedDate> proposedDates;
    /**
     * 参加者リスト
     */
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    @OrderBy("participantPk.nickname ASC")
    private List<YParticipant> participants;
    /**
     * 合言葉
     */
    @Embedded
    private Aikotoba aikotoba;
    /**
     * 作成タイムスタンプ
     */
    //@NotNull
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime createdAt;
    /**
     * 更新タイムスタンプ
     */
    //@NotNull
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime updatedAt;
}
