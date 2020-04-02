package com.vaadin.tests.layouts.layouttester.VLayout;

import java.util.Iterator;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.layouts.layouttester.BaseLayoutForSpacingMargin;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

public class VLayoutMarginSpacing extends BaseLayoutForSpacingMargin {

    public VLayoutMarginSpacing() {
        super(VerticalLayout.class);
    }

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
        Iterator<Component> iterator = l2.iterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            if (component instanceof Table) {
                component.setSizeUndefined();
            } else if (component instanceof Label) {
                component.setWidth("100%");
            }
        }
    }

    @Override
    protected void setLayoutMeasures(AbstractOrderedLayout l1,
            AbstractOrderedLayout l2, String w, String h) {
        l1.setSizeUndefined();
        l2.setSizeUndefined();
    }

}
