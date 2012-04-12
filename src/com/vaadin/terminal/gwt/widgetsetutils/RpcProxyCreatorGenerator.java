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
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.client.communication.InitializableServerRpc;
import com.vaadin.terminal.gwt.client.communication.RpcProxy.RpcProxyCreator;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;

public class RpcProxyCreatorGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext ctx,
            String requestedClassName) throws UnableToCompleteException {
        logger.log(TreeLogger.DEBUG, "Running RpcProxyCreatorGenerator");
        TypeOracle typeOracle = ctx.getTypeOracle();
        assert (typeOracle != null);

        JClassType requestedType = typeOracle.findType(requestedClassName);
        String packageName = requestedType.getPackage().getName();
        String className = requestedType.getSimpleSourceName() + "Impl";
        if (requestedType == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
                    + requestedClassName + "'", null);
            throw new UnableToCompleteException();
        }

        createType(logger, ctx, packageName, className);
        return packageName + "." + className;
    }

    private void createType(TreeLogger logger, GeneratorContext context,
            String packageName, String className) {
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(
                packageName, className);

        PrintWriter printWriter = context.tryCreate(logger,
                composer.getCreatedPackage(),
                composer.getCreatedClassShortName());
        if (printWriter == null) {
            // print writer is null if source code has already been generated
            return;
        }
        Date date = new Date();
        TypeOracle typeOracle = context.getTypeOracle();

        // init composer, set class properties, create source writer
        composer.addImport(GWT.class.getCanonicalName());
        composer.addImport(ServerRpc.class.getCanonicalName());
        composer.addImport(ServerConnector.class.getCanonicalName());
        composer.addImport(InitializableServerRpc.class.getCanonicalName());
        composer.addImport(IllegalArgumentException.class.getCanonicalName());
        composer.addImplementedInterface(RpcProxyCreator.class
                .getCanonicalName());

        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);
        sourceWriter.indent();

        sourceWriter
                .println("public <T extends ServerRpc> T create(Class<T> rpcInterface, ServerConnector connector) {");
        sourceWriter.indent();

        sourceWriter
                .println("if (rpcInterface == null || connector == null) {");
        sourceWriter.indent();
        sourceWriter
                .println("throw new IllegalArgumentException(\"RpcInterface and/or connector cannot be null\");");
        sourceWriter.outdent();

        JClassType initializableInterface = typeOracle.findType(ServerRpc.class
                .getCanonicalName());

        for (JClassType rpcType : initializableInterface.getSubtypes()) {
            String rpcClassName = rpcType.getQualifiedSourceName();
            if (InitializableServerRpc.class.getCanonicalName().equals(
                    rpcClassName)) {
                // InitializableClientToServerRpc is a special marker interface
                // that should not get a generated class
                continue;
            }
            sourceWriter.println("} else if (rpcInterface == " + rpcClassName
                    + ".class) {");
            sourceWriter.indent();
            sourceWriter.println(rpcClassName + " rpc = GWT.create("
                    + rpcClassName + ".class);");
            sourceWriter.println("((" + InitializableServerRpc.class.getName()
                    + ") rpc).initRpc(connector);");
            sourceWriter.println("return (T) rpc;");
            sourceWriter.outdent();
        }

        sourceWriter.println("} else {");
        sourceWriter.indent();
        sourceWriter
                .println("throw new IllegalArgumentException(\"No RpcInterface of type \"+ rpcInterface.getName() + \" was found.\");");
        sourceWriter.outdent();
        // End of if
        sourceWriter.println("}");
        // End of method
        sourceWriter.println("}");

        // close generated class
        sourceWriter.outdent();
        sourceWriter.println("}");
        // commit generated class
        context.commit(logger, printWriter);
        logger.log(Type.INFO, composer.getCreatedClassName() + " created in "
                + (new Date().getTime() - date.getTime()) / 1000 + "seconds");

    }
}
