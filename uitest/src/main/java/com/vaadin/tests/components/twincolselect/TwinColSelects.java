package com.vaadin.tests.components.twincolselect;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.ui.TwinColSelect;

public class TwinColSelects extends AbstractSelectTestCase<TwinColSelect> {

    @Override
    protected Class<TwinColSelect> getTestClass() {
        return TwinColSelect.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createRowSelectAction();
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

        super.createSelectAction("Rows", CATEGORY_DATA_SOURCE, options, "-",
                rowsAction);

    }

    private Command<TwinColSelect, Integer> rowsAction = new Command<TwinColSelect, Integer>() {

        @Override
        public void execute(TwinColSelect c, Integer value, Object data) {
            c.setRows(value);
        }
    };
}
