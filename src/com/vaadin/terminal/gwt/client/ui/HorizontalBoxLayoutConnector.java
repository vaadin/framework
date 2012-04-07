package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Component.LoadStyle;
import com.vaadin.ui.HorizontalLayout;

@Component(value = HorizontalLayout.class, loadStyle = LoadStyle.EAGER)
public class HorizontalBoxLayoutConnector extends AbstractBoxLayoutConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        getWidget().setVertical(false);
    }

}
