package com.vaadin.tests.layouts.layouttester.VLayout;

import com.vaadin.tests.layouts.layouttester.BaseComponentSizing;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;

public class VComponentSizing extends BaseComponentSizing {

    public VComponentSizing() {
        super(VerticalLayout.class);
    }

    @Override
    protected void setLayoutMeasures(AbstractOrderedLayout l1,
            AbstractOrderedLayout l2, String w, String h) {
        l1.setWidth("400px");
        l1.setHeight("-1px");
        l2.setWidth("400px");
        l2.setHeight("800px");
    }

}
