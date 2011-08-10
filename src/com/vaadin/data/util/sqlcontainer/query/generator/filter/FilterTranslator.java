package com.vaadin.data.util.sqlcontainer.query.generator.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

public interface FilterTranslator {
    public boolean translatesFilter(Filter filter);

    public String getWhereStringForFilter(Filter filter, StatementHelper sh);

}
