package yachosan.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposedDate implements Serializable {
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @NotNull
    private LocalDate startDate;

    @Override
    public String toString() {
        return startDate.toString();
    }
}
