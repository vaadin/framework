package com.vaadin.tests.components.listselect;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.ui.ListSelect;

public class ListSelects extends AbstractSelectTestCase<ListSelect> {

    @Override
    protected Class<ListSelect> getTestClass() {
        return ListSelect.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createColumnSelectAction();
        createRowSelectAction();
    }

    private void createColumnSelectAction() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("-", 0);
        for (int i = 1; i <= 10; i++) {
            options.put(String.valueOf(i), i);
        }
        options.put("50", 50);
        options.put("100", 100);
        options.put("1000", 1000);

        super.createSelectAction("Columns", CATEGORY_CONTENT, options, "-",
                columnsAction);

    }

    private void createRowSelectAction() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("-", 0);
        for (int i = 1; i <= 10; i++) {
            options.put(String.valueOf(i), i);
        }
        options.put("50", 50);
        options.put("100", 100);
        options.put("1000", 1000);

        super.createSelectAction("Rows", CATEGORY_CONTENT, options, "-",
                rowsAction);

    }

    private Command<ListSelect, Integer> columnsAction = new Command<ListSelect, Integer>() {

        public void execute(ListSelect c, Integer value, Object data) {
            c.setColumns(value);
        }
    };
    private Command<ListSelect, Integer> rowsAction = new Command<ListSelect, Integer>() {

        public void execute(ListSelect c, Integer value, Object data) {
            c.setRows(value);
        }
    };

}
