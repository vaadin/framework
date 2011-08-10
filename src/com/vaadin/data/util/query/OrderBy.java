package com.vaadin.data.util.query;

import java.io.Serializable;

/**
 * OrderBy represents a sorting rule to be applied to a query made by the
 * SQLContainer's QueryDelegate.
 * 
 * The sorting rule is simple and contains only the affected column's name and
 * the direction of the sort.
 */
public class OrderBy implements Serializable {
    private String column;
    private boolean isAscending;

    /**
     * Prevent instantiation without required parameters.
     */
    @SuppressWarnings("unused")
    private OrderBy() {
    }

    public OrderBy(String column, boolean isAscending) {
        setColumn(column);
        setAscending(isAscending);
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }

    public void setAscending(boolean isAscending) {
        this.isAscending = isAscending;
    }

    public boolean isAscending() {
        return isAscending;
    }
}
