package com.vaadin.data.util.query.generator.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.query.generator.StatementHelper;

public class AndTranslator implements FilterTranslator {

    public boolean translatesFilter(Filter filter) {
        return filter instanceof And;
    }

    public String getWhereStringForFilter(Filter filter, StatementHelper sh) {
        return QueryBuilder.group(QueryBuilder
                .getJoinedFilterString(((And) filter).getFilters(), "AND", sh));
    }

}
