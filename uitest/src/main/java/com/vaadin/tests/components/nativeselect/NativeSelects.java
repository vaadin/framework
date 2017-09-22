package com.vaadin.tests.components.nativeselect;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.abstractlisting.AbstractSingleSelectTestUI;
import com.vaadin.ui.NativeSelect;

public class NativeSelects
        extends AbstractSingleSelectTestUI<NativeSelect<Object>> {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Class<NativeSelect<Object>> getTestClass() {
        return (Class) NativeSelect.class;
    }

    @Override
    protected NativeSelect<Object> constructComponent() {
        NativeSelect<Object> component = super.constructComponent();
        component.setEmptySelectionAllowed(false);
        return component;
    }

    @Override
    protected void createActions() {
        super.createActions();
        LinkedHashMap<String, Integer> options = new LinkedHashMap<>();
        options.put("1", 1);
        options.put("2", 2);
        options.put("5", 5);
        createSelectAction("Visible item count", CATEGORY_SIZE, options, "1",
                new Command<NativeSelect<Object>, Integer>() {
                    @Override
                    public void execute(NativeSelect<Object> c, Integer value,
                            Object data) {
                        c.setVisibleItemCount(value);
                    }
                });
    }
}
