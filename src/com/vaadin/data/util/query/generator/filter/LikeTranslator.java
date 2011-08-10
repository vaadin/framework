package com.vaadin.data.util.query.generator.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.query.generator.StatementHelper;

public class LikeTranslator implements FilterTranslator {

    public boolean translatesFilter(Filter filter) {
        return filter instanceof Like;
    }

    public String getWhereStringForFilter(Filter filter, StatementHelper sh) {
        Like like = (Like) filter;
        if (like.isCaseSensitive()) {
            sh.addParameterValue(like.getValue());
            return QueryBuilder.quote(like.getPropertyId())
                    + " LIKE ?";
        } else {
            sh.addParameterValue(like.getValue().toUpperCase());
            return "UPPER("
                    + QueryBuilder.quote(like.getPropertyId())
                    + ") LIKE ?";
        }
    }

}
