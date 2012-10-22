package com.vaadin.tests.components.orderedlayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class OrderedLayoutSlotStyleNames extends TestBase {

    @Override
    protected void setup() {
        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(new Label("A label"));
        vl.addComponent(new Button("Button"));
        addComponent(vl);

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(new Label("A label"));
        hl.addComponent(new Button("Button"));
        addComponent(hl);
    }

    @Override
    protected String getDescription() {
        return "Vertical/HorizontalLayout slots should get child dependant name";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9051;
    }

}
