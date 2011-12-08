/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util.sqlcontainer.query.generator.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

public class IsNullTranslator implements FilterTranslator {

    public boolean translatesFilter(Filter filter) {
        return filter instanceof IsNull;
    }

    public String getWhereStringForFilter(Filter filter, StatementHelper sh) {
        IsNull in = (IsNull) filter;
        return QueryBuilder.quote(in.getPropertyId()) + " IS NULL";
    }
}
