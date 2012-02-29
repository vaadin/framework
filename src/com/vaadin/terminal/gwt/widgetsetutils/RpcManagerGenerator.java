/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.communication.ClientRpc;
import com.vaadin.terminal.gwt.client.communication.MethodInvocation;
import com.vaadin.terminal.gwt.client.communication.RpcManager;

/**
 * GWT generator that creates an implementation for {@link RpcManager} on the
 * client side classes for executing RPC calls received from the the server.
 * 
 * @since 7.0
 */
public class RpcManagerGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
            String typeName) throws UnableToCompleteException {

        String packageName = null;
        String className = null;
        try {
            TypeOracle typeOracle = context.getTypeOracle();

            // get classType and save instance variables
            JClassType classType = typeOracle.getType(typeName);
            packageName = classType.getPackage().getName();
            className = classType.getSimpleSourceName() + "Impl";
            // Generate class source code for SerializerMapImpl
            generateClass(logger, context, packageName, className);
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR,
                    "SerializerMapGenerator creation failed", e);
        }
        // return the fully qualifed name of the class generated
        return packageName + "." + className;
    }

    /**
     * Generate source code for RpcManagerImpl
     * 
     * @param logger
     *            Logger object
     * @param context
     *            Generator context
     * @param packageName
     *            package name for the class to generate
     * @param className
     *            class name for the class to generate
     */
    private void generateClass(TreeLogger logger, GeneratorContext context,
            String packageName, String className) {
        // get print writer that receives the source code
        PrintWriter printWriter = null;
        printWriter = context.tryCreate(logger, packageName, className);
        // print writer if null, source code has ALREADY been generated
        if (printWriter == null) {
            return;
        }
        logger.log(Type.INFO,
                "Detecting server to client RPC interface types...");
        Date date = new Date();
        TypeOracle typeOracle = context.getTypeOracle();
        JClassType serverToClientRpcType = typeOracle.findType(ClientRpc.class
                .getName());
        JClassType[] rpcInterfaceSubtypes = serverToClientRpcType.getSubtypes();

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport("com.google.gwt.core.client.GWT");
        composer.addImplementedInterface(RpcManager.class.getName());
        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);
        sourceWriter.indent();

        List<JClassType> rpcInterfaces = new ArrayList<JClassType>();

        // iterate over RPC interfaces and create helper methods for each
        // interface
        for (JClassType type : rpcInterfaceSubtypes) {
            if (null == type.isInterface()) {
                // only interested in interfaces here, not implementations
                continue;
            }
            rpcInterfaces.add(type);
            // generate method to call methods of an RPC interface
            sourceWriter.println("private void " + getInvokeMethodName(type)
                    + "(" + MethodInvocation.class.getName() + " invocation, "
                    + ConnectorMap.class.getName() + " connectorMap) {");
            sourceWriter.indent();

            // loop over the methods of the interface and its superinterfaces
            // methods
            for (JClassType currentType : type.getFlattenedSupertypeHierarchy()) {
                for (JMethod method : currentType.getMethods()) {
                    sourceWriter.println("if (\"" + method.getName()
                            + "\".equals(invocation.getMethodName())) {");
                    sourceWriter.indent();
                    // construct parameter string with appropriate casts
                    String paramString = "";
                    JType[] parameterTypes = method.getParameterTypes();
                    for (int i = 0; i < parameterTypes.length; ++i) {
                        paramString = paramString + "("
                                + parameterTypes[i].getQualifiedSourceName()
                                + ") invocation.getParameters()[i]";
                        if (i < parameterTypes.length - 1) {
                            paramString = paramString + ", ";
                        }
                    }
                    sourceWriter
                            .println(Connector.class.getName()
                                    + " connector = connectorMap.getConnector(invocation.getConnectorId());");
                    sourceWriter
                            .println("for ("
                                    + ClientRpc.class.getName()
                                    + " rpcImplementation : connector.getRpcImplementations(\""
                                    + type.getQualifiedSourceName() + "\")) {");
                    sourceWriter.indent();
                    sourceWriter.println("((" + type.getQualifiedSourceName()
                            + ") rpcImplementation)." + method.getName() + "("
                            + paramString + ");");
                    sourceWriter.outdent();
                    sourceWriter.println("}");
                    sourceWriter.println("return;");
                    sourceWriter.outdent();
                    sourceWriter.println("}");
                }
            }

            sourceWriter.outdent();
            sourceWriter.println("}");

            logger.log(Type.DEBUG,
                    "Constructed helper method for server to client RPC for "
                            + type.getName());
        }

        // generate top-level "switch-case" method to select the correct
        // previously generated method based on the RPC interface
        sourceWriter.println("public void applyInvocation("
                + MethodInvocation.class.getName() + " invocation, "
                + ConnectorMap.class.getName() + " connectorMap) {");
        sourceWriter.indent();

        for (JClassType type : rpcInterfaces) {
            sourceWriter.println("if (\"" + type.getQualifiedSourceName()
                    + "\".equals(invocation.getInterfaceName())) {");
            sourceWriter.indent();
            sourceWriter.println(getInvokeMethodName(type)
                    + "(invocation, connectorMap);");
            sourceWriter.println("return;");
            sourceWriter.outdent();
            sourceWriter.println("}");

            logger.log(Type.INFO,
                    "Configured server to client RPC for " + type.getName());
        }
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

    }

    private String getInvokeMethodName(JClassType type) {
        return "invoke" + type.getQualifiedSourceName().replaceAll("\\.", "_");
    }
}
