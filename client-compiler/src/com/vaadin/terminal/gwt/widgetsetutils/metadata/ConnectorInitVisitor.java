/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.vaadin.shared.ui.Connect;

public class ConnectorInitVisitor extends TypeVisitor {

    @Override
    public void visit(JClassType type, ConnectorBundle bundle) {
        if (isConnectedConnector(type)) {
            Connect connectAnnotation = type.getAnnotation(Connect.class);
            bundle.setIdentifier(type, connectAnnotation.value()
                    .getCanonicalName());
            bundle.setNeedsGwtConstructor(type);
        }
    }

}
