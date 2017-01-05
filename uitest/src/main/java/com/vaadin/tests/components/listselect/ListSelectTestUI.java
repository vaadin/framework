package com.vaadin.tests.components.listselect;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.abstractlisting.AbstractMultiSelectTestUI;
import com.vaadin.ui.ListSelect;

public class ListSelectTestUI
        extends AbstractMultiSelectTestUI<ListSelect<Object>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class<ListSelect<Object>> getTestClass() {
        return (Class) ListSelect.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createRows();
    };

    private void createRows() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<>();
        options.put("0", 0);
        options.put("1", 1);
        options.put("2", 2);
        options.put("5", 5);
        options.put("10 (default)", 10);
        options.put("50", 50);

        createSelectAction("Rows", CATEGORY_STATE, options, "10 (default)",
                (c, value, data) -> c.setRows(value), null);
    }
}
