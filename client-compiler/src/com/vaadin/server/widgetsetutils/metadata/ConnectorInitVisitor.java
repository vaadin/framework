/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.server.widgetsetutils.metadata;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.vaadin.shared.ui.Connect;

public class ConnectorInitVisitor extends TypeVisitor {

    @Override
    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        Connect connectAnnotation = type.getAnnotation(Connect.class);
        if (connectAnnotation != null) {
            logger.log(Type.INFO, type.getName() + " will be in the "
                    + bundle.getName().replaceAll("^_*", "") + " bundle");
            String identifier = connectAnnotation.value().getCanonicalName();

            bundle.setIdentifier(type, identifier);
            bundle.setNeedsGwtConstructor(type);
        }
    }

}
