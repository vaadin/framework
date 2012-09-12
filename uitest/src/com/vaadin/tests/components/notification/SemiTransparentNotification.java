package com.vaadin.tests.components.notification;

import com.vaadin.server.Page;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class SemiTransparentNotification extends TestBase {

    @Override
    protected void setup() {
        Label l = new Label(LoremIpsum.get(10000));
        getLayout().setSizeFull();
        addComponent(l);

        Notification n = new Notification(
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This should be a <br/><b>SEMI-TRANSPARENT</b><br/> notification",
                Type.WARNING_MESSAGE);
        n.setHtmlContentAllowed(true);
        n.show(Page.getCurrent());
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
