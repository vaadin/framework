/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.server.widgetsetutils.metadata;

import java.util.Map;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.dev.util.collect.HashMap;
import com.vaadin.shared.ui.Connect;

public class ConnectorInitVisitor extends TypeVisitor {

    private Map<String, JClassType> processedConnections = new HashMap<String, JClassType>();

    @Override
    public void visitConnector(TreeLogger logger, JClassType type,
            ConnectorBundle bundle) throws UnableToCompleteException {
        Connect connectAnnotation = type.getAnnotation(Connect.class);
        if (connectAnnotation != null) {
            logger.log(Type.INFO, type.getName() + " will be in the "
                    + bundle.getName().replaceAll("^_*", "") + " bundle");
            String identifier = connectAnnotation.value().getCanonicalName();

            JClassType previousMapping = processedConnections.put(identifier,
                    type);
            if (previousMapping != null) {
                logger.log(
                        Type.ERROR,
                        "Multiple @Connect mappings detected for " + identifier
                                + ": " + type.getQualifiedSourceName()
                                + " and "
                                + previousMapping.getQualifiedSourceName());
                throw new UnableToCompleteException();
            }

            bundle.setIdentifier(type, identifier);
            bundle.setNeedsGwtConstructor(type);
        }
    }

}
