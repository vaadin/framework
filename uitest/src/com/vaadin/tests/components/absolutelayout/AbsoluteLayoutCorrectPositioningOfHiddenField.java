package com.vaadin.tests.components.absolutelayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

public class AbsoluteLayoutCorrectPositioningOfHiddenField extends TestBase {

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        AbsoluteLayout abs = new AbsoluteLayout();
        abs.setStyleName("borders");
        abs.setWidth("250px");
        abs.setHeight("100px");

        final Label l = new Label("Positioned at 20,20");
        l.setSizeUndefined();
        l.setId("positionedLabel");
        l.setVisible(false);
        abs.addComponent(l, "top:20px;left:20px");

        Button show = new Button("Set visible", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                l.setVisible(true);
            }
        });
        show.setId("showLabelButton");
        abs.addComponent(show, "top: 70px;left: 150px;");

        addComponent(abs);
    }

    @Override
    protected String getDescription() {
        return "AbsoluteLayout should reposition invisible components when set to visible";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10180;
    }

}
