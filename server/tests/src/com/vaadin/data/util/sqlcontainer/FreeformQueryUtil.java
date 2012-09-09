package com.vaadin.data.util.sqlcontainer;

import java.util.List;

import org.junit.Test;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.SQLTestsConstants.DB;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

public class FreeformQueryUtil {

    @Test
    public void testDummy() {
        // Added dummy test so JUnit will not complain about
        // "No runnable methods".
    }

    public static StatementHelper getQueryWithFilters(List<Filter> filters,
            int offset, int limit) {
        StatementHelper sh = new StatementHelper();
        if (SQLTestsConstants.db == DB.MSSQL) {
            if (limit > 1) {
                offset++;
                limit--;
            }
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM (SELECT row_number() OVER (");
            query.append("ORDER BY \"ID\" ASC");
            query.append(") AS rownum, * FROM \"PEOPLE\"");

            if (!filters.isEmpty()) {
                query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
            }
            query.append(") AS a WHERE a.rownum BETWEEN ").append(offset)
                    .append(" AND ").append(Integer.toString(offset + limit));
            sh.setQueryString(query.toString());
            return sh;
        } else if (SQLTestsConstants.db == DB.ORACLE) {
            if (limit > 1) {
                offset++;
                limit--;
            }
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM (SELECT x.*, ROWNUM AS "
                    + "\"rownum\" FROM (SELECT * FROM \"PEOPLE\"");
            if (!filters.isEmpty()) {
                query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
            }
            query.append(") x) WHERE \"rownum\" BETWEEN ? AND ?");
            sh.addParameterValue(offset);
            sh.addParameterValue(offset + limit);
            sh.setQueryString(query.toString());
            return sh;
        } else {
            StringBuilder query = new StringBuilder("SELECT * FROM people");
            if (!filters.isEmpty()) {
                query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
            }
            if (limit != 0 || offset != 0) {
                query.append(" LIMIT ? OFFSET ?");
                sh.addParameterValue(limit);
                sh.addParameterValue(offset);
            }
            sh.setQueryString(query.toString());
            return sh;
        }
    }

}
