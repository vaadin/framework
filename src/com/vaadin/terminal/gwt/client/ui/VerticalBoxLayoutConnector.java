package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Component.LoadStyle;
import com.vaadin.ui.VerticalLayout;

@Component(value = VerticalLayout.class, loadStyle = LoadStyle.EAGER)
public class VerticalBoxLayoutConnector extends AbstractBoxLayoutConnector {

    @Override
    public void init() {
        super.init();
        getWidget().setVertical(true);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // TODO fix when Vaadin style name handling is improved so that it won't
        // override extra client side style names
        getWidget().setVertical(true);
        super.updateFromUIDL(uidl, client);
        getWidget().setVertical(true);
    }

}
