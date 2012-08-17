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
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JArrayType;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.communication.JSONSerializer;
import com.vaadin.terminal.gwt.client.communication.SerializerMap;

/**
 * GWT generator that creates a {@link SerializerMap} implementation (mapper
 * from type string to serializer instance) and serializer classes for all
 * subclasses of {@link SharedState}.
 * 
 * @since 7.0
 */
public class SerializerMapGenerator extends Generator {

    private static final String FAIL_IF_NOT_SERIALIZABLE = "vFailIfNotSerializable";
    private String packageName;
    private String className;

    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
            String typeName) throws UnableToCompleteException {

        try {
            TypeOracle typeOracle = context.getTypeOracle();
            Set<JClassType> typesNeedingSerializers = findTypesNeedingSerializers(
                    typeOracle, logger);
            checkForUnserializableTypes(typesNeedingSerializers, typeOracle,
                    logger);
            Set<JClassType> typesWithExistingSerializers = findTypesWithExistingSerializers(
                    typeOracle, logger);
            Set<JClassType> serializerMappings = new HashSet<JClassType>();
            serializerMappings.addAll(typesNeedingSerializers);
            serializerMappings.addAll(typesWithExistingSerializers);
            // get classType and save instance variables
            JClassType classType = typeOracle.getType(typeName);
            packageName = classType.getPackage().getName();
            className = classType.getSimpleSourceName() + "Impl";
            // Generate class source code for SerializerMapImpl
            generateSerializerMap(serializerMappings, logger, context);

            SerializerGenerator sg = new SerializerGenerator();
            for (JClassType type : typesNeedingSerializers) {
                sg.generate(logger, context, type.getQualifiedSourceName());
            }
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR,
                    "SerializerMapGenerator creation failed", e);
            throw new UnableToCompleteException();
        }
        // return the fully qualifed name of the class generated
        return packageName + "." + className;
    }

    /**
     * Emits a warning for all classes that are used in communication but do not
     * implement java.io.Serializable. Implementing java.io.Serializable is not
     * needed for communication but for the server side Application to be
     * serializable i.e. work in GAE for instance.
     * 
     * @param typesNeedingSerializers
     * @param typeOracle
     * @param logger
     * @throws UnableToCompleteException
     */
    private void checkForUnserializableTypes(
            Set<JClassType> typesNeedingSerializers, TypeOracle typeOracle,
            TreeLogger logger) throws UnableToCompleteException {
        JClassType javaSerializable = typeOracle.findType(Serializable.class
                .getName());
        for (JClassType type : typesNeedingSerializers) {
            if (type.isArray() != null) {
                // Don't check for arrays
                continue;
            }
            boolean serializable = type.isAssignableTo(javaSerializable);
            if (!serializable) {
                boolean abortCompile = "true".equals(System
                        .getProperty(FAIL_IF_NOT_SERIALIZABLE));
                logger.log(
                        abortCompile ? Type.ERROR : Type.WARN,
                        type
                                + " is used in RPC or shared state but does not implement "
                                + Serializable.class.getName()
                                + ". Communication will work but the Application on server side cannot be serialized if it refers to objects of this type. "
                                + "If the system property "
                                + FAIL_IF_NOT_SERIALIZABLE
                                + " is set to \"true\", this causes the compilation to fail instead of just emitting a warning.");
                if (abortCompile) {
                    throw new UnableToCompleteException();
                }
            }
        }
    }

    private Set<JClassType> findTypesWithExistingSerializers(
            TypeOracle typeOracle, TreeLogger logger)
            throws UnableToCompleteException {
        JClassType serializerInterface = typeOracle
                .findType(JSONSerializer.class.getName());
        JType[] deserializeParamTypes = new JType[] {
                typeOracle
                        .findType(com.vaadin.terminal.gwt.client.metadata.Type.class
                                .getName()),
                typeOracle.findType(JSONValue.class.getName()),
                typeOracle.findType(ApplicationConnection.class.getName()) };
        String deserializeMethodName = "deserialize";
        try {
            serializerInterface.getMethod(deserializeMethodName,
                    deserializeParamTypes);
        } catch (NotFoundException e) {
            logger.log(Type.ERROR, "Could not find " + deserializeMethodName
                    + " in " + serializerInterface);
            throw new UnableToCompleteException();
        }

        Set<JClassType> types = new HashSet<JClassType>();
        for (JClassType serializer : serializerInterface.getSubtypes()) {
            JMethod deserializeMethod = serializer.findMethod(
                    deserializeMethodName, deserializeParamTypes);
            if (deserializeMethod == null) {
                logger.log(Type.DEBUG, "Could not find "
                        + deserializeMethodName + " in " + serializer);
                continue;
            }
            JType returnType = deserializeMethod.getReturnType();
            logger.log(Type.DEBUG, "Found " + deserializeMethodName
                    + " with return type " + returnType + " in " + serializer);

            types.add(returnType.isClass());
        }
        return types;
    }

    /**
     * Generate source code for SerializerMapImpl
     * 
     * @param typesNeedingSerializers
     * 
     * @param logger
     *            Logger object
     * @param context
     *            Generator context
     */
    private void generateSerializerMap(Set<JClassType> typesNeedingSerializers,
            TreeLogger logger, GeneratorContext context) {
        // get print writer that receives the source code
        PrintWriter printWriter = null;
        printWriter = context.tryCreate(logger, packageName, className);
        // print writer if null, source code has ALREADY been generated
        if (printWriter == null) {
            return;
        }
        Date date = new Date();
        TypeOracle typeOracle = context.getTypeOracle();

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport("com.google.gwt.core.client.GWT");
        composer.addImplementedInterface(SerializerMap.class.getName());
        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);
        sourceWriter.indent();

        sourceWriter.println("public " + JSONSerializer.class.getName()
                + " getSerializer(String type) {");
        sourceWriter.indent();

        // TODO cache serializer instances in a map
        for (JClassType type : typesNeedingSerializers) {
            sourceWriter.print("if (type.equals(\""
                    + type.getQualifiedSourceName() + "\")");
            if (type instanceof JArrayType) {
                // Also add binary name to support encoding based on
                // object.getClass().getName()
                sourceWriter.print("||type.equals(\"" + type.getJNISignature()
                        + "\")");
            }
            sourceWriter.println(") {");
            sourceWriter.indent();
            String serializerName = SerializerGenerator
                    .getFullyQualifiedSerializerClassName(type);
            sourceWriter.println("return GWT.create(" + serializerName
                    + ".class);");
            sourceWriter.outdent();
            sourceWriter.println("}");
            logger.log(Type.INFO, "Configured serializer (" + serializerName
                    + ") for " + type.getName());
        }
        sourceWriter
                .println("throw new RuntimeException(\"No serializer found for class \"+type);");
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

    public Set<JClassType> findTypesNeedingSerializers(TypeOracle typeOracle,
            TreeLogger logger) {
        logger.log(Type.DEBUG, "Detecting serializable data types...");

        HashSet<JClassType> types = new HashSet<JClassType>();

        // Generate serializer classes for each subclass of SharedState
        JClassType serializerType = typeOracle.findType(SharedState.class
                .getName());
        types.add(serializerType);
        JClassType[] serializerSubtypes = serializerType.getSubtypes();
        for (JClassType type : serializerSubtypes) {
            types.add(type);
        }

        // Serializer classes might also be needed for RPC methods
        for (Class<?> cls : new Class[] { ServerRpc.class, ClientRpc.class }) {
            JClassType rpcType = typeOracle.findType(cls.getName());
            JClassType[] serverRpcSubtypes = rpcType.getSubtypes();
            for (JClassType type : serverRpcSubtypes) {
                addMethodParameterTypes(type, types, logger);
            }
        }

        // Add all types used from/in the types
        for (Object t : types.toArray()) {
            findSubTypesNeedingSerializers((JClassType) t, types);
        }
        logger.log(Type.DEBUG, "Serializable data types: " + types.toString());

        return types;
    }

    private void addMethodParameterTypes(JClassType classContainingMethods,
            Set<JClassType> types, TreeLogger logger) {
        for (JMethod method : classContainingMethods.getMethods()) {
            if (method.getName().equals("initRpc")) {
                continue;
            }
            for (JType type : method.getParameterTypes()) {
                addTypeIfNeeded(types, type);
            }
        }
    }

    public void findSubTypesNeedingSerializers(JClassType type,
            Set<JClassType> serializableTypes) {
        // Find all setters and look at their parameter type to determine if a
        // new serializer is needed
        for (JMethod setterMethod : SerializerGenerator.getSetters(type)) {
            // The one and only parameter for the setter
            JType setterType = setterMethod.getParameterTypes()[0];
            addTypeIfNeeded(serializableTypes, setterType);
        }
    }

    private void addTypeIfNeeded(Set<JClassType> serializableTypes, JType type) {
        if (serializableTypes.contains(type)) {
            return;
        }
        JParameterizedType parametrized = type.isParameterized();
        if (parametrized != null) {
            for (JClassType parameterType : parametrized.getTypeArgs()) {
                addTypeIfNeeded(serializableTypes, parameterType);
            }
        }

        if (serializationHandledByFramework(type)) {
            return;
        }

        if (serializableTypes.contains(type)) {
            return;
        }

        JClassType typeClass = type.isClass();
        if (typeClass != null) {
            // setterTypeClass is null at least for List<String>. It is
            // possible that we need to handle the cases somehow, for
            // instance for List<MyObject>.
            serializableTypes.add(typeClass);
            findSubTypesNeedingSerializers(typeClass, serializableTypes);
        }

        // Generate (n-1)-dimensional array serializer for n-dimensional array
        JArrayType arrayType = type.isArray();
        if (arrayType != null) {
            serializableTypes.add(arrayType);
            addTypeIfNeeded(serializableTypes, arrayType.getComponentType());
        }

    }

    Set<Class<?>> frameworkHandledTypes = new HashSet<Class<?>>();
    {
        frameworkHandledTypes.add(String.class);
        frameworkHandledTypes.add(Boolean.class);
        frameworkHandledTypes.add(Integer.class);
        frameworkHandledTypes.add(Float.class);
        frameworkHandledTypes.add(Double.class);
        frameworkHandledTypes.add(Long.class);
        frameworkHandledTypes.add(Enum.class);
        frameworkHandledTypes.add(String[].class);
        frameworkHandledTypes.add(Object[].class);
        frameworkHandledTypes.add(Map.class);
        frameworkHandledTypes.add(List.class);
        frameworkHandledTypes.add(Set.class);
        frameworkHandledTypes.add(Byte.class);
        frameworkHandledTypes.add(Character.class);

    }

    private boolean serializationHandledByFramework(JType setterType) {
        // Some types are handled by the framework at the moment. See #8449
        // This method should be removed at some point.
        if (setterType.isPrimitive() != null) {
            return true;
        }

        String qualifiedName = setterType.getQualifiedSourceName();
        for (Class<?> cls : frameworkHandledTypes) {
            if (qualifiedName.equals(cls.getName())) {
                return true;
            }
        }

        return false;
    }
}
