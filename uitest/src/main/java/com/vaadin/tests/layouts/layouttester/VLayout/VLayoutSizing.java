package com.vaadin.tests.layouts.layouttester.VLayout;

import com.vaadin.tests.layouts.layouttester.BaseLayoutSizing;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;

public class VLayoutSizing extends BaseLayoutSizing {

    public VLayoutSizing() {
        super(VerticalLayout.class);
    }

    @Override
    protected void setLayoutMeasures(AbstractOrderedLayout l1,
            AbstractOrderedLayout l2, String w, String h) {
        l1.setWidth("400px");
        l1.setHeight("-1px");
        l2.setWidth("400px");
        l2.setHeight("500px");
    }
}
