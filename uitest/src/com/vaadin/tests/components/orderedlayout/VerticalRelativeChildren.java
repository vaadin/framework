package com.vaadin.tests.components.orderedlayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class VerticalRelativeChildren extends TestBase {

    @Override
    protected void setup() {

        VerticalLayout vl = new VerticalLayout();
        vl.setWidth("300px");

        TextArea areaUndefined = new TextArea();
        areaUndefined.setSizeUndefined();
        areaUndefined.setValue("Undefined height");
        vl.addComponent(areaUndefined);

        TextArea areaDefined = new TextArea();
        areaDefined.setWidth("200px");
        areaDefined.setValue("200px width");
        vl.addComponent(areaDefined);

        TextArea areaRelativeBottom = new TextArea();
        areaRelativeBottom.setWidth("50%");
        areaRelativeBottom.setValue("50% width, right align");
        vl.addComponent(areaRelativeBottom);
        vl.setComponentAlignment(areaRelativeBottom, Alignment.TOP_RIGHT);

        TextArea areaRelativeCenter = new TextArea();
        areaRelativeCenter.setWidth("50%");
        areaRelativeCenter.setValue("50% width, center align");
        vl.addComponent(areaRelativeCenter);
        vl.setComponentAlignment(areaRelativeCenter, Alignment.TOP_CENTER);

        TextArea areaRelativeTop = new TextArea();
        areaRelativeTop.setWidth("50%");
        areaRelativeTop.setValue("50% width, left align");
        vl.addComponent(areaRelativeTop);
        vl.setComponentAlignment(areaRelativeTop, Alignment.TOP_LEFT);

        addComponent(vl);
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
