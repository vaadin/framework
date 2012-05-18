/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.PrintWriter;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.client.ui.ConnectorClassBasedFactory;
import com.vaadin.terminal.gwt.client.ui.ConnectorClassBasedFactory.Creator;

/**
 * GWT generator that creates a lookup method for
 * {@link ConnectorClassBasedFactory} instances.
 * 
 * @since 7.0
 */
public abstract class AbstractConnectorClassBasedFactoryGenerator extends
        Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
            String typeName) throws UnableToCompleteException {

        try {
            // get classType and save instance variables
            return generateConnectorClassBasedFactory(typeName, logger, context);
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR, typeName + " creation failed", e);
            throw new UnableToCompleteException();
        }
    }

    private String generateConnectorClassBasedFactory(String typeName,
            TreeLogger logger, GeneratorContext context)
            throws NotFoundException {
        TypeOracle typeOracle = context.getTypeOracle();

        JClassType classType = typeOracle.getType(typeName);
        String superName = classType.getSimpleSourceName();
        String packageName = classType.getPackage().getName();
        String className = superName + "Impl";

        // get print writer that receives the source code
        PrintWriter printWriter = null;
        printWriter = context.tryCreate(logger, packageName, className);
        // print writer if null, source code has ALREADY been generated
        if (printWriter == null) {
            return packageName + "." + className;
        }

        Date date = new Date();

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport(GWT.class.getName());
        composer.addImport(Creator.class.getCanonicalName());
        composer.setSuperclass(superName);

        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);
        sourceWriter.indent();

        // public ConnectorStateFactoryImpl() {
        sourceWriter.println("public " + className + "() {");
        sourceWriter.indent();

        JClassType serverConnectorType = typeOracle.getType(getConnectorType()
                .getCanonicalName());
        for (JClassType connector : serverConnectorType.getSubtypes()) {
            // addCreator(TextAreaConnector.class, new Creator<SharedState>() {
            if (connector.isInterface() != null || connector.isAbstract()) {
                continue;
            }

            JClassType targetType = getTargetType(connector);
            if (targetType.isAbstract()) {
                continue;
            }

            sourceWriter.println("addCreator("
                    + connector.getQualifiedSourceName()
                    + ".class, new Creator<"
                    + targetType.getQualifiedSourceName() + ">() {");
            // public SharedState create() {
            sourceWriter.println("public "
                    + targetType.getQualifiedSourceName() + " create() {");
            // return GWT.create(TextAreaState.class);
            sourceWriter.println("return GWT.create("
                    + targetType.getQualifiedSourceName() + ".class);");
            // }
            sourceWriter.println("}");
            // });
            sourceWriter.println("});");
        }

        // End of constructor
        sourceWriter.outdent();
        sourceWriter.println("}");

        // close generated class
        sourceWriter.outdent();
        sourceWriter.println("}");

        // commit generated class
        context.commit(logger, printWriter);
        logger.log(Type.INFO,
                "Done. (" + (new Date().getTime() - date.getTime()) / 1000
                        + "seconds)");
        return packageName + "." + className;

    }

    protected abstract Class<? extends ServerConnector> getConnectorType();

    protected abstract JClassType getTargetType(JClassType connectorType);

    protected JClassType getGetterReturnType(JClassType connector,
            String getterName) {
        try {
            JMethod getMethod = connector.getMethod(getterName, new JType[] {});
            return (JClassType) getMethod.getReturnType();
        } catch (NotFoundException e) {
            return getGetterReturnType(connector.getSuperclass(), getterName);
        }

    }

}
