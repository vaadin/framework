/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data.util.sqlcontainer.query.generator.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

public class QueryBuilder implements Serializable {

    private static ArrayList<FilterTranslator> filterTranslators = new ArrayList<FilterTranslator>();
    private static StringDecorator stringDecorator = new StringDecorator("\"",
            "\"");

    static {
        /* Register all default filter translators */
        addFilterTranslator(new AndTranslator());
        addFilterTranslator(new OrTranslator());
        addFilterTranslator(new LikeTranslator());
        addFilterTranslator(new BetweenTranslator());
        addFilterTranslator(new CompareTranslator());
        addFilterTranslator(new NotTranslator());
        addFilterTranslator(new IsNullTranslator());
        addFilterTranslator(new SimpleStringTranslator());
    }

    public synchronized static void addFilterTranslator(
            FilterTranslator translator) {
        filterTranslators.add(translator);
    }

    /**
     * Allows specification of a custom ColumnQuoter instance that handles
     * quoting of column names for the current DB dialect.
     * 
     * @param decorator
     *            the ColumnQuoter instance to use.
     */
    public static void setStringDecorator(StringDecorator decorator) {
        stringDecorator = decorator;
    }

    public static String quote(Object str) {
        return stringDecorator.quote(str);
    }

    public static String group(String str) {
        return stringDecorator.group(str);
    }

    /**
     * Constructs and returns a string representing the filter that can be used
     * in a WHERE clause.
     * 
     * @param filter
     *            the filter to translate
     * @param sh
     *            the statement helper to update with the value(s) of the filter
     * @return a string representing the filter.
     */
    public synchronized static String getWhereStringForFilter(Filter filter,
            StatementHelper sh) {
        for (FilterTranslator ft : filterTranslators) {
            if (ft.translatesFilter(filter)) {
                return ft.getWhereStringForFilter(filter, sh);
            }
        }
        return "";
    }

    public static String getJoinedFilterString(Collection<Filter> filters,
            String joinString, StatementHelper sh) {
        StringBuilder result = new StringBuilder();
        for (Filter f : filters) {
            result.append(getWhereStringForFilter(f, sh));
            result.append(" ").append(joinString).append(" ");
        }
        // Remove the last instance of joinString
        result.delete(result.length() - joinString.length() - 2,
                result.length());
        return result.toString();
    }

    public static String getWhereStringForFilters(List<Filter> filters,
            StatementHelper sh) {
        if (filters == null || filters.isEmpty()) {
            return "";
        }
        StringBuilder where = new StringBuilder(" WHERE ");
        where.append(getJoinedFilterString(filters, "AND", sh));
        return where.toString();
    }
}
