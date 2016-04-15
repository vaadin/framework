package com.vaadin.tests.components.absolutelayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class AbsoluteLayoutWrapperStyles extends TestBase {

    @Override
    protected void setup() {
        AbsoluteLayout layout = new AbsoluteLayout();
        layout.setWidth("500px");
        layout.setHeight("500px");

        Label lbl = new Label("Label");
        lbl.setStyleName("my-label");
        lbl.addStyleName("my-second-label");
        layout.addComponent(lbl);

        Button btn = new Button("Button");
        btn.setStyleName("my-button");
        btn.addStyleName("my-second-button");
        layout.addComponent(btn, "top:50px;");

        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "Absolutelayout wrapper should get child stylenames";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9051;
    }

}
