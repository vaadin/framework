/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ServerConnector;

/**
 * GWT generator that creates a Widget class for a given Connector class, based
 * on the return type of getWidget()
 * 
 * @since 7.0
 */
public class ConnectorWidgetFactoryGenerator extends
        AbstractConnectorClassBasedFactoryGenerator {
    @Override
    protected JClassType getTargetType(JClassType connectorType) {
        return getGetterReturnType(connectorType, "getWidget");
    }

    @Override
    protected Class<? extends ServerConnector> getConnectorType() {
        return ComponentConnector.class;
    }

}