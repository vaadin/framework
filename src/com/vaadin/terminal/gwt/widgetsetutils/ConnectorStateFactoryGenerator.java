/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.vaadin.terminal.gwt.client.ServerConnector;

/**
 * GWT generator that creates a SharedState class for a given Connector class,
 * based on the return type of getState()
 * 
 * @since 7.0
 */
public class ConnectorStateFactoryGenerator extends
        AbstractConnectorClassBasedFactoryGenerator {

    @Override
    protected JClassType getTargetType(JClassType connectorType) {
        return getGetterReturnType(connectorType, "getState");
    }

    @Override
    protected Class<? extends ServerConnector> getConnectorType() {
        return ServerConnector.class;
    }

}
