/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.gwt.client.ServerConnector;

public class ConnectorInitVisitor implements TypeVisitor {

    private JClassType serverConnector;

    @Override
    public void init(TypeOracle oracle) throws NotFoundException {
        serverConnector = oracle.getType(ServerConnector.class.getName());
    }

    @Override
    public void visit(JClassType type, ConnectorBundle bundle) {
        Connect connectAnnotation = type.getAnnotation(Connect.class);
        if (connectAnnotation != null && serverConnector.isAssignableFrom(type)) {
            bundle.setIdentifier(type, connectAnnotation.value()
                    .getCanonicalName());
            bundle.setNeedsGwtConstructor(type);
        }
    }

}
