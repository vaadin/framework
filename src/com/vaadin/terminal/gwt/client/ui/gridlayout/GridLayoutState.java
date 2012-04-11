/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.gridlayout;

import com.vaadin.terminal.gwt.client.ui.AbstractLayoutState;

public class GridLayoutState extends AbstractLayoutState {
    private boolean spacing = false;
    private int rows = 0;
    private int columns = 0;

    public boolean isSpacing() {
        return spacing;
    }

    public void setSpacing(boolean spacing) {
        this.spacing = spacing;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int cols) {
        columns = cols;
    }

}