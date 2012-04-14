package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Component.LoadStyle;
import com.vaadin.ui.HorizontalLayout;

@Component(value = HorizontalLayout.class, loadStyle = LoadStyle.EAGER)
public class HorizontalBoxLayoutConnector extends AbstractBoxLayoutConnector {

    @Override
    public void init() {
        super.init();
        getWidget().setVertical(false);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // TODO remove when Vaadin style name handling is improved so that it
        // won't override extra client side style names
        getWidget().setVertical(false);
        super.updateFromUIDL(uidl, client);
        getWidget().setVertical(false);
    }

}
