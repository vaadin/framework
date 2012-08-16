/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.terminal.gwt.client.communication.GeneratedRpcMethodProvider;
import com.vaadin.terminal.gwt.client.communication.RpcManager;
import com.vaadin.terminal.gwt.client.communication.RpcMethod;

/**
 * GWT generator that creates an implementation for {@link RpcManager} on the
 * client side classes for executing RPC calls received from the the server.
 * 
 * @since 7.0
 */
public class GeneratedRpcMethodProviderGenerator extends Generator {

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
        composer.addImport(RpcMethod.class.getName());
        composer.addImport(ClientRpc.class.getName());
        composer.addImport(com.vaadin.terminal.gwt.client.communication.Type.class
                .getName());
        composer.addImplementedInterface(GeneratedRpcMethodProvider.class
                .getName());
        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);
        sourceWriter.indent();

        List<JMethod> rpcMethods = new ArrayList<JMethod>();

        sourceWriter
                .println("public java.util.Collection<RpcMethod> getGeneratedRpcMethods() {");
        sourceWriter.indent();

        sourceWriter
                .println("java.util.ArrayList<RpcMethod> list = new java.util.ArrayList<RpcMethod>();");

        // iterate over RPC interfaces and create helper methods for each
        // interface
        for (JClassType type : rpcInterfaceSubtypes) {
            if (null == type.isInterface()) {
                // only interested in interfaces here, not implementations
                continue;
            }

            // loop over the methods of the interface and its superinterfaces
            // methods
            for (JClassType currentType : type.getFlattenedSupertypeHierarchy()) {
                for (JMethod method : currentType.getMethods()) {

                    // RpcMethod(String interfaceName, String methodName,
                    // Type... parameterTypes)
                    sourceWriter.print("list.add(new RpcMethod(\""
                            + type.getQualifiedSourceName() + "\", \""
                            + method.getName() + "\"");
                    JType[] parameterTypes = method.getParameterTypes();
                    for (JType parameter : parameterTypes) {
                        sourceWriter.print(", ");
                        writeTypeCreator(sourceWriter, parameter);
                    }
                    sourceWriter.println(") {");
                    sourceWriter.indent();

                    sourceWriter
                            .println("public void applyInvocation(ClientRpc target, Object... parameters) {");
                    sourceWriter.indent();

                    sourceWriter.print("((" + type.getQualifiedSourceName()
                            + ")target)." + method.getName() + "(");
                    for (int i = 0; i < parameterTypes.length; i++) {
                        JType parameterType = parameterTypes[i];
                        if (i != 0) {
                            sourceWriter.print(", ");
                        }
                        String parameterTypeName = getBoxedTypeName(parameterType);
                        sourceWriter.print("(" + parameterTypeName
                                + ") parameters[" + i + "]");
                    }
                    sourceWriter.println(");");

                    sourceWriter.outdent();
                    sourceWriter.println("}");

                    sourceWriter.outdent();
                    sourceWriter.println("});");
                }
            }
        }

        sourceWriter.println("return list;");

        sourceWriter.outdent();
        sourceWriter.println("}");
        sourceWriter.println();

        // close generated class
        sourceWriter.outdent();
        sourceWriter.println("}");
        // commit generated class
        context.commit(logger, printWriter);
        logger.log(Type.INFO,
                "Done. (" + (new Date().getTime() - date.getTime()) / 1000
                        + "seconds)");

    }

    public static void writeTypeCreator(SourceWriter sourceWriter, JType type) {
        String typeName = getBoxedTypeName(type);
        sourceWriter.print("new Type(\"" + typeName + "\", ");
        JParameterizedType parameterized = type.isParameterized();
        if (parameterized != null) {
            sourceWriter.print("new Type[] {");
            JClassType[] typeArgs = parameterized.getTypeArgs();
            for (JClassType jClassType : typeArgs) {
                writeTypeCreator(sourceWriter, jClassType);
                sourceWriter.print(", ");
            }
            sourceWriter.print("}");
        } else {
            sourceWriter.print("null");
        }
        sourceWriter.print(")");
    }

    public static String getBoxedTypeName(JType type) {
        if (type.isPrimitive() != null) {
            // Used boxed types for primitives
            return type.isPrimitive().getQualifiedBoxedSourceName();
        } else {
            return type.getErasedType().getQualifiedSourceName();
        }
    }

    private String getInvokeMethodName(JClassType type) {
        return "invoke" + type.getQualifiedSourceName().replaceAll("\\.", "_");
    }
}
