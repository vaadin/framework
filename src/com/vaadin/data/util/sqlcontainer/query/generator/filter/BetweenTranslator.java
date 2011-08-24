/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util.sqlcontainer.query.generator.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

public class BetweenTranslator implements FilterTranslator {

    public boolean translatesFilter(Filter filter) {
        return filter instanceof Between;
    }

    public String getWhereStringForFilter(Filter filter, StatementHelper sh) {
        Between between = (Between) filter;
        sh.addParameterValue(between.getStartValue());
        sh.addParameterValue(between.getEndValue());
        return QueryBuilder.quote(between.getPropertyId()) + " BETWEEN ? AND ?";
    }

}
