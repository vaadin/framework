package com.vaadin.tests.components.orderedlayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;

public class HorizontalLayoutVerticalAlign extends TestBase {

    @Override
    protected void setup() {
        HorizontalLayout p = new HorizontalLayout();

        p.addComponent(new TextArea());

        Label top = new Label("top");
        p.addComponent(top);
        p.setComponentAlignment(top, Alignment.TOP_CENTER);

        Label middle = new Label("middle");
        p.addComponent(middle);
        p.setComponentAlignment(middle, Alignment.MIDDLE_CENTER);

        Label bottom = new Label("bottom");
        p.addComponent(bottom);
        p.setComponentAlignment(bottom, Alignment.BOTTOM_CENTER);

        p.addComponent(new TextArea());

        addComponent(p);
    }

    @Override
    protected String getDescription() {
        return "Vertical alignments should be top-middle-bottom";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10852;
    }

}
