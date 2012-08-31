package com.vaadin.tests.components.select;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.TwinColSelect;

public class TwinColSelects extends ComponentTestCase<TwinColSelect> {

    @Override
    protected Class<TwinColSelect> getTestClass() {
        return TwinColSelect.class;
    }

    @Override
    protected void initializeComponents() {

        TwinColSelect tws = createTwinColSelect("400x<auto>");
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

    private TwinColSelect createTwinColSelect(String caption) {
        TwinColSelect select = new TwinColSelect(caption);
        select.addContainerProperty(CAPTION, String.class, null);
        for (int i = 0; i < 20; i++) {
            select.addItem("" + i).getItemProperty(CAPTION)
                    .setValue("Item " + i);
        }
        select.setImmediate(true);
        return select;
    }

}
