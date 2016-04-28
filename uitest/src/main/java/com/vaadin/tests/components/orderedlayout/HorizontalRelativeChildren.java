package com.vaadin.tests.components.orderedlayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;

public class HorizontalRelativeChildren extends TestBase {

    @Override
    protected void setup() {

        HorizontalLayout hl = new HorizontalLayout();

        TextArea areaUndefined = new TextArea();
        areaUndefined.setSizeUndefined();
        areaUndefined.setValue("Undefined height");
        hl.addComponent(areaUndefined);

        TextArea areaDefined = new TextArea();
        areaDefined.setHeight("200px");
        areaDefined.setValue("200px height");
        hl.addComponent(areaDefined);

        TextArea areaRelativeBottom = new TextArea();
        areaRelativeBottom.setHeight("50%");
        areaRelativeBottom.setValue("50% height, bottom align");
        hl.addComponent(areaRelativeBottom);
        hl.setComponentAlignment(areaRelativeBottom, Alignment.BOTTOM_LEFT);

        TextArea areaRelativeCenter = new TextArea();
        areaRelativeCenter.setHeight("50%");
        areaRelativeCenter.setValue("50% height, center align");
        hl.addComponent(areaRelativeCenter);
        hl.setComponentAlignment(areaRelativeCenter, Alignment.MIDDLE_LEFT);

        TextArea areaRelativeTop = new TextArea();
        areaRelativeTop.setHeight("50%");
        areaRelativeTop.setValue("50% height, top align");
        hl.addComponent(areaRelativeTop);
        hl.setComponentAlignment(areaRelativeTop, Alignment.TOP_LEFT);

        addComponent(hl);
    }

    @Override
    protected String getDescription() {
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
