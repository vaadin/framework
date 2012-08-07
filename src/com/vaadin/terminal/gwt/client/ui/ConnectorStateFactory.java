/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.vaadin.shared.Connector;
import com.vaadin.shared.communication.SharedState;

public abstract class ConnectorStateFactory extends
        ConnectorClassBasedFactory<SharedState> {
    private static ConnectorStateFactory impl = null;

    /**
     * Creates a SharedState using GWT.create for the given connector, based on
     * its {@link AbstractComponentConnector#getSharedState ()} return type.
     * 
     * @param connector
     * @return
     */
    public static SharedState createState(Class<? extends Connector> connector) {
        return getImpl().create(connector);
    }

    private static ConnectorStateFactory getImpl() {
        if (impl == null) {
            impl = GWT.create(ConnectorStateFactory.class);
        }
        return impl;
    }
}
