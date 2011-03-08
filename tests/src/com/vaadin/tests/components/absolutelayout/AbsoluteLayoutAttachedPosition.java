package com.vaadin.tests.components.absolutelayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.ComponentContainer.ComponentAttachEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window.Notification;


public class AbsoluteLayoutAttachedPosition extends TestBase {

    @Override
    protected void setup() {
        final AbsoluteLayout a = new AbsoluteLayout();
        a.setWidth("300px");
        a.setHeight("300px");
        a.addListener(new ComponentContainer.ComponentAttachListener() {
            public void componentAttachedToContainer(ComponentAttachEvent event) {
                AbsoluteLayout layout = (AbsoluteLayout) event.getContainer();
                AbsoluteLayout.ComponentPosition position = layout
                        .getPosition(event.getAttachedComponent());

                getMainWindow().showNotification(
                        "Attached to " + position.getCSSString(),
                        Notification.TYPE_ERROR_MESSAGE);
            }
        });
        addComponent(a);

        addComponent(new Button("Attach label to AbsoluteLayout",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        a.addComponent(new Label("X"), "top:50px;left:50px");
                    }
                }));
    }

    @Override
    protected String getDescription() {
        return "When pressing the attach button a Label with the value \"X\" "
                + "should get add to the above placed AbsoluteLayout. The position css "
                + "should be 'top:50px;left:50px'.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6368;
    }

}
