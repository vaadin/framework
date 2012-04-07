package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Component.LoadStyle;
import com.vaadin.ui.VerticalLayout;

@Component(value = VerticalLayout.class, loadStyle = LoadStyle.EAGER)
public class VerticalBoxLayoutConnector extends AbstractBoxLayoutConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        getWidget().setVertical(true);
    }

}
