package yachosan.infra.model.proposeddate;

import org.dozer.DozerConverter;
import yachosan.domain.model.ProposedDate;

public class ProposedDateDozerConverter extends DozerConverter<ProposedDate, ProposedDate> {
    public ProposedDateDozerConverter() {
        super(ProposedDate.class, ProposedDate.class);
    }

    @Override
    public ProposedDate convertTo(ProposedDate source, ProposedDate destination) {
        if (source == null) return null;
        return new ProposedDate(source.getStartDate());
    }

    @Override
    public ProposedDate convertFrom(ProposedDate source, ProposedDate destination) {
        if (source == null) return null;
        return new ProposedDate(source.getStartDate());
    }
}
