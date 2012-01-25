package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.communication.ClientToServerRpc;
import com.vaadin.terminal.gwt.client.communication.ClientToServerRpc.InitializableClientToServerRpc;
import com.vaadin.terminal.gwt.client.communication.MethodInvocation;

/**
 * GWT generator that creates client side proxy classes for making RPC calls
 * from the client to the server.
 * 
 * GWT.create() calls for interfaces extending {@link ClientToServerRpc} are
 * affected, and a proxy implementation is created. Note that the init(...)
 * method of the proxy must be called before the proxy is used.
 * 
 * @since 7.0
 */
public class RpcProxyGenerator extends Generator {
    @Override
    public String generate(TreeLogger logger, GeneratorContext ctx,
            String requestedClassName) throws UnableToCompleteException {
        Type logType = TreeLogger.INFO;

        logger.log(TreeLogger.INFO, "Running RpcProxyGenerator", null);

        TypeOracle typeOracle = ctx.getTypeOracle();
        assert (typeOracle != null);

        JClassType requestedType = typeOracle.findType(requestedClassName);
        if (requestedType == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
                    + requestedClassName + "'", null);
            throw new UnableToCompleteException();
        }

        String generatedClassName = "ClientToServerRpc_"
                + requestedType.getName().replaceAll("[$.]", "_");

        JClassType initializableInterface = typeOracle
                .findType(InitializableClientToServerRpc.class
                        .getCanonicalName());

        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(
                requestedType.getPackage().getName(), generatedClassName);
        composer.addImplementedInterface(requestedType.getQualifiedSourceName());
        composer.addImplementedInterface(initializableInterface
                .getQualifiedSourceName());
        composer.addImport(MethodInvocation.class.getCanonicalName());

        logger.log(logType,
                "Generating client proxy for remote service interface '"
                        + requestedType.getQualifiedSourceName() + "'");
        PrintWriter printWriter = ctx.tryCreate(logger,
                composer.getCreatedPackage(),
                composer.getCreatedClassShortName());
        if (printWriter != null) {
            SourceWriter writer = composer.createSourceWriter(ctx, printWriter);

            // constructor
            writer.println("public " + generatedClassName + "() {}");

            // initialization etc.
            writeCommonFieldsAndMethods(logger, writer, typeOracle);

            // actual proxy methods forwarding calls to the server
            writeRemoteProxyMethods(logger, writer, typeOracle, requestedType,
                    requestedType.isClassOrInterface().getMethods());

            // End of class
            writer.outdent();
            writer.println("}");

            ctx.commit(logger, printWriter);
        }

        return composer.getCreatedClassName();
    }

    private void writeCommonFieldsAndMethods(TreeLogger logger,
            SourceWriter writer, TypeOracle typeOracle) {
        JClassType applicationConnectionClass = typeOracle
                .findType(ApplicationConnection.class.getCanonicalName());

        // fields
        writer.println("private String paintableId;");
        writer.println("private "
                + applicationConnectionClass.getQualifiedSourceName()
                + " client;");

        // init method from the RPC interface
        writer.println("public void initRpc(String paintableId, "
                + applicationConnectionClass.getQualifiedSourceName()
                + " client) {");
        writer.indent();

        writer.println("this.paintableId = paintableId;");
        writer.println("this.client = client;");

        writer.outdent();
        writer.println("}");
    }

    private static void writeRemoteProxyMethods(TreeLogger logger,
            SourceWriter writer, TypeOracle typeOracle,
            JClassType requestedType, JMethod[] methods) {
        for (JMethod m : methods) {
            writer.print(m.getReadableDeclaration(false, false, false, false,
                    true));
            writer.println(" {");
            writer.indent();

            writer.print("client.addMethodInvocationToQueue(new MethodInvocation(paintableId, \""
                    + requestedType.getQualifiedBinaryName() + "\", \"");
            writer.print(m.getName());
            writer.print("\", new Object[] {");
            // new Object[] { ... } for parameters - autoboxing etc. by the
            // compiler
            JParameter[] parameters = m.getParameters();
            boolean first = true;
            for (JParameter p : parameters) {
                if (!first) {
                    writer.print(", ");
                }
                first = false;

                writer.print(p.getName());
            }
            writer.println("}), true);");

            writer.outdent();
            writer.println("}");
        }
    }
}
