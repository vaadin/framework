package com.vaadin.data.util.sqlcontainer.query.generator.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

public class OrTranslator implements FilterTranslator {

    public boolean translatesFilter(Filter filter) {
        return filter instanceof Or;
    }

    public String getWhereStringForFilter(Filter filter, StatementHelper sh) {
        return QueryBuilder.group(QueryBuilder
                .getJoinedFilterString(((Or) filter).getFilters(), "OR", sh));
    }

}
