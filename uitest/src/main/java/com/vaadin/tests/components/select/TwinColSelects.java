package com.vaadin.tests.components.select;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.TwinColSelect;

public class TwinColSelects extends ComponentTestCase<TwinColSelect> {

    @Override
    protected Class<TwinColSelect> getTestClass() {
        return TwinColSelect.class;
    }

    @Override
    protected void initializeComponents() {

        TwinColSelect<String> tws = createTwinColSelect("400x<auto>");
        tws.setWidth("400px");
        tws.setHeight("-1px");
        addTestComponent(tws);

        tws = createTwinColSelect("400x100");
        tws.setWidth("400px");
        tws.setHeight("100px");
        addTestComponent(tws);

        tws = createTwinColSelect("<auto>x100");
        tws.setWidth("-1px");
        tws.setHeight("100px");
        addTestComponent(tws);

        tws = createTwinColSelect("<auto>x<auto>");
        tws.setSizeUndefined();
        addTestComponent(tws);

    }

    private TwinColSelect<String> createTwinColSelect(String caption) {
        TwinColSelect<String> select = new TwinColSelect<>(caption);
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            items.add("Item " + i);
        }
        select.setItems(items);
        return select;
    }

}
