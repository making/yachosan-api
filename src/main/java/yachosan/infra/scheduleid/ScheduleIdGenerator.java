package yachosan.infra.scheduleid;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import yachosan.domain.model.ScheduleId;

import java.io.Serializable;
import java.util.UUID;

public class ScheduleIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        return ScheduleId.of(UUID.randomUUID().toString());
    }
}