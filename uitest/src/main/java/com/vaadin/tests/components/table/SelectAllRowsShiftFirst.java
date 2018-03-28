package com.vaadin.tests.components.table;

public class SelectAllRowsShiftFirst extends SelectAllRows {

    @Override
    protected String getTestDescription() {
        return "Selecting all rows does not work by pressing shift and selecting the first row, and then press shift then select last row";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13483;
    }

}
