package com.vaadin.tests.dd;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.dd.CustomDDImplementation.MyDragSource;

@Connect(MyDragSource.class)
public class MyDragSourceConnector extends AbstractComponentConnector
        implements Paintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }
    }

    @Override
    public VMyDragSource getWidget() {
        return (VMyDragSource) super.getWidget();
    }

}
