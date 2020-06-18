package com.vaadin.tests.layouts.layouttester.HLayout;

import com.vaadin.tests.layouts.layouttester.BaseIcon;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.HorizontalLayout;

public class HIcon extends BaseIcon {

    public HIcon() {
        super(HorizontalLayout.class);
    }

    @Override
    protected void setLayoutMeasures(AbstractOrderedLayout l1,
            AbstractOrderedLayout l2, String w, String h) {
        super.setLayoutMeasures(l1, l2, "2900px", "200px");
    }

}
