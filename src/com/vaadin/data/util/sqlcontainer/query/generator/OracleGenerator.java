/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util.sqlcontainer.query.generator;

import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

@SuppressWarnings("serial")
public class OracleGenerator extends DefaultSQLGenerator {

    public OracleGenerator() {

    }

    /**
     * Construct an OracleSQLGenerator with the specified identifiers for start
     * and end of quoted strings. The identifiers may be different depending on
     * the database engine and it's settings.
     * 
     * @param quoteStart
     *            the identifier (character) denoting the start of a quoted
     *            string
     * @param quoteEnd
     *            the identifier (character) denoting the end of a quoted string
     */
    public OracleGenerator(String quoteStart, String quoteEnd) {
        super(quoteStart, quoteEnd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.generator.DefaultSQLGenerator#
     * generateSelectQuery(java.lang.String, java.util.List,
     * com.vaadin.addon.sqlcontainer.query.FilteringMode, java.util.List, int,
     * int, java.lang.String)
     */
    @Override
    public StatementHelper generateSelectQuery(String tableName,
            List<Filter> filters, List<OrderBy> orderBys, int offset,
            int pagelength, String toSelect) {
        if (tableName == null || tableName.trim().equals("")) {
            throw new IllegalArgumentException("Table name must be given.");
        }
        /* Adjust offset and page length parameters to match "row numbers" */
        offset = pagelength > 1 ? ++offset : offset;
        pagelength = pagelength > 1 ? --pagelength : pagelength;
        toSelect = toSelect == null ? "*" : toSelect;
        StatementHelper sh = new StatementHelper();
        StringBuffer query = new StringBuffer();

        /* Row count request is handled here */
        if ("COUNT(*)".equalsIgnoreCase(toSelect)) {
            query.append(String.format(
                    "SELECT COUNT(*) AS %s FROM (SELECT * FROM %s",
                    QueryBuilder.quote("rowcount"), tableName));
            if (filters != null && !filters.isEmpty()) {
                query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
            }
            query.append(")");
            sh.setQueryString(query.toString());
            return sh;
        }

        /* SELECT without row number constraints */
        if (offset == 0 && pagelength == 0) {
            query.append("SELECT ").append(toSelect).append(" FROM ")
                    .append(tableName);
            if (filters != null) {
                query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
            }
            if (orderBys != null) {
                for (OrderBy o : orderBys) {
                    generateOrderBy(query, o, orderBys.indexOf(o) == 0);
                }
            }
            sh.setQueryString(query.toString());
            return sh;
        }

        /* Remaining SELECT cases are handled here */
        query.append(String
                .format("SELECT * FROM (SELECT x.*, ROWNUM AS %s FROM (SELECT %s FROM %s",
                        QueryBuilder.quote("rownum"), toSelect, tableName));
        if (filters != null) {
            query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
        }
        if (orderBys != null) {
            for (OrderBy o : orderBys) {
                generateOrderBy(query, o, orderBys.indexOf(o) == 0);
            }
        }
        query.append(String.format(") x) WHERE %s BETWEEN %d AND %d",
                QueryBuilder.quote("rownum"), offset, offset + pagelength));
        sh.setQueryString(query.toString());
        return sh;
    }
}