package com.vaadin.data.util.query.generator.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.query.generator.StatementHelper;

public class CompareTranslator implements FilterTranslator {

    public boolean translatesFilter(Filter filter) {
        return filter instanceof Compare;
    }

    public String getWhereStringForFilter(Filter filter, StatementHelper sh) {
        Compare compare = (Compare) filter;
        sh.addParameterValue(compare.getValue());
        String prop = QueryBuilder.quote(compare.getPropertyId());
        switch (compare.getOperation()) {
        case EQUAL:
            return prop + " = ?";
        case GREATER:
            return prop + " > ?";
        case GREATER_OR_EQUAL:
            return prop + " >= ?";
        case LESS:
            return prop + " < ?";
        case LESS_OR_EQUAL:
            return prop + " <= ?";
        default:
            return "";
        }
    }

}
