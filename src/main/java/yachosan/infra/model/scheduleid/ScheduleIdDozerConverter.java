package yachosan.infra.model.scheduleid;

import org.dozer.DozerConverter;
import yachosan.domain.model.ScheduleId;

public class ScheduleIdDozerConverter extends DozerConverter<ScheduleId, ScheduleId> {
    public ScheduleIdDozerConverter() {
        super(ScheduleId.class, ScheduleId.class);
    }

    @Override
    public ScheduleId convertTo(ScheduleId source, ScheduleId destination) {
        if (source == null) return null;
        return ScheduleId.of(source.getValue());
    }

    @Override
    public ScheduleId convertFrom(ScheduleId source, ScheduleId destination) {
        if (source == null) return null;
        return ScheduleId.of(source.getValue());
    }
}
