package com.vaadin.tests.components.absolutelayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

public class AbsoluteLayoutCorrectPositioningOfHiddenField extends TestBase {

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        final AbsoluteLayout abs = new AbsoluteLayout();
        abs.setStyleName("borders");
        abs.setWidth("250px");
        abs.setHeight("100px");

        final Label l = new Label("Top 20, Left 20");
        l.setSizeUndefined();
        l.setId("positionedLabel");
        l.setVisible(false);
        abs.addComponent(l, "top:20px;left:20px");

        final Button action = new Button("Set visible");
        action.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (l.isVisible()) {
                    l.setValue("Top 70, Left 20");
                    ComponentPosition position = abs.getPosition(l);
                    position.setCSSString("top:70px;left:20px;");
                    abs.setPosition(l, position);
                } else {
                    l.setVisible(true);
                    action.setCaption("Move down");
                }
            }
        });
        action.setId("actionButton");
        abs.addComponent(action, "top: 70px;left: 150px;");

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
