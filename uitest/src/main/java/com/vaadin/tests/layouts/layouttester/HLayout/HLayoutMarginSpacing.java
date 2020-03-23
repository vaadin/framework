package com.vaadin.tests.layouts.layouttester.HLayout;

import java.util.Iterator;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.layouts.layouttester.BaseLayoutForSpacingMargin;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Table;

public class HLayoutMarginSpacing extends BaseLayoutForSpacingMargin {

    public HLayoutMarginSpacing() {
        super(HorizontalLayout.class);
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
                component.setWidth("30px");
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
