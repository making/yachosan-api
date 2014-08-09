package yachosan.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "y_participant")
@ToString(exclude = "schedule")
public class YParticipant implements Serializable {
    /**
     * 複合キー(ニックネーム、スケジュールID)
     */
    @EmbeddedId
    @JsonUnwrapped
    private ParticipantPk participantPk;

    /**
     * パスワード
     */
    @Size(max = 255)
    private char[] password;
    /**
     * 一言
     */
    @Size(min = 1, max = 200)
    private String comment;
    /**
     * 回答リスト
     */
    @ElementCollection
    @Enumerated(value = EnumType.STRING)
    @Column(length = 10)
    @CollectionTable(name = "y_participant_replies",
            joinColumns = {@JoinColumn(name = "nickname"), @JoinColumn(name = "schedule_id")})
    @OrderBy("start_date")
    private Map<ProposedDate, Reply> replies;
    /**
     * リマインド用メールアドレス
     */
    @Email
    @Size(max = 127)
    private String email;
    /**
     * スケジュール
     */
    @ManyToOne
    @JoinColumn(name = "schedule_id", insertable = false, updatable = false)
    @JsonIgnore
    private YSchedule schedule;
}
