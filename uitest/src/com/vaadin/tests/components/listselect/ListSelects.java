package com.vaadin.tests.components.listselect;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.ui.ListSelect;

public class ListSelects extends AbstractSelectTestCase<ListSelect> {

    private Command<ListSelect, Integer> rowsCommand = new Command<ListSelect, Integer>() {
        @Override
        public void execute(ListSelect c, Integer value, Object data) {
            c.setRows(value);
        }
    };

    private Command<ListSelect, Integer> colsCommand = new Command<ListSelect, Integer>() {
        @Override
        public void execute(ListSelect c, Integer value, Object data) {
            c.setColumns(value);
        }
    };

    @Override
    protected Class<ListSelect> getTestClass() {
        return ListSelect.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createRowsAction(CATEGORY_FEATURES);
        createColsAction(CATEGORY_FEATURES);
    }

    private void createRowsAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(20);
        createSelectAction("Rows", category, options, "0", rowsCommand);
    }

    private void createColsAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(20);
        createSelectAction("Columns", category, options, "0", colsCommand);
    }

}
