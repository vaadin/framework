/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.textfield.TextFieldConnector;
import com.vaadin.terminal.gwt.client.ui.textfield.VTextField;

public abstract class ConnectorWidgetFactory extends
        ConnectorClassBasedFactory<Widget> {
    private static ConnectorWidgetFactory impl = null;

    // TODO Move to generator
    {
        addCreator(TextFieldConnector.class, new Creator<Widget>() {
            public Widget create() {
                return GWT.create(VTextField.class);
            }
        });
    }

    /**
     * Creates a widget using GWT.create for the given connector, based on its
     * {@link AbstractComponentConnector#getWidget()} return type.
     * 
     * @param connector
     * @return
     */
    public static Widget createWidget(
            Class<? extends AbstractComponentConnector> connector) {
        return getImpl().create(connector);
    }

    private static ConnectorWidgetFactory getImpl() {
        if (impl == null) {
            impl = GWT.create(ConnectorWidgetFactory.class);
        }
        return impl;
    }
}
