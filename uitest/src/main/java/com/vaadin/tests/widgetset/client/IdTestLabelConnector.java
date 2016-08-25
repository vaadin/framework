package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.ui.Connect;
import com.vaadin.v7.client.ui.label.LabelConnector;

/**
 * Connects server-side <code>IdTestLabel</code> component to client-side
 * {@link VIdTestLabel} component (#10179).
 *
 */
@Connect(com.vaadin.tests.widgetset.server.IdTestLabel.class)
public class IdTestLabelConnector extends LabelConnector {

    @Override
    public VIdTestLabel getWidget() {
        return (VIdTestLabel) super.getWidget();
    }
}
