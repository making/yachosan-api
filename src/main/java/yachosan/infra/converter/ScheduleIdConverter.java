package yachosan.infra.converter;

import org.springframework.core.convert.converter.Converter;
import yachosan.domain.model.ScheduleId;

public class ScheduleIdConverter implements Converter<String, ScheduleId> {
    @Override
    public ScheduleId convert(String s) {
        return ScheduleId.of(s);
    }
}
