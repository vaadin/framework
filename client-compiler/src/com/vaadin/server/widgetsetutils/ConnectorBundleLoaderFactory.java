/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.server.widgetsetutils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.metadata.ConnectorBundleLoader;
import com.vaadin.client.metadata.InvokationHandler;
import com.vaadin.client.metadata.ProxyHandler;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.client.metadata.TypeDataStore;
import com.vaadin.client.ui.UnknownComponentConnector;
import com.vaadin.server.widgetsetutils.metadata.ClientRpcVisitor;
import com.vaadin.server.widgetsetutils.metadata.ConnectorBundle;
import com.vaadin.server.widgetsetutils.metadata.ConnectorInitVisitor;
import com.vaadin.server.widgetsetutils.metadata.GeneratedSerializer;
import com.vaadin.server.widgetsetutils.metadata.Property;
import com.vaadin.server.widgetsetutils.metadata.ServerRpcVisitor;
import com.vaadin.server.widgetsetutils.metadata.StateInitVisitor;
import com.vaadin.server.widgetsetutils.metadata.TypeVisitor;
import com.vaadin.server.widgetsetutils.metadata.WidgetInitVisitor;
import com.vaadin.shared.annotations.Delayed;
import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

public class ConnectorBundleLoaderFactory extends Generator {
    /**
     * Special SourceWriter that approximates the number of written bytes to
     * support splitting long methods into shorter chunks to avoid hitting the
     * 65535 byte limit.
     */
    private class SplittingSourceWriter implements SourceWriter {
        private final SourceWriter target;
        private final String baseName;
        private final int splitSize;

        // Seems to be undercounted by about 15%
        private int approximateChars = 0;
        private int wrapCount = 0;

        public SplittingSourceWriter(SourceWriter target, String baseName,
                int splitSize) {
            this.target = target;
            this.baseName = baseName;
            this.splitSize = splitSize;
        }

        @Override
        public void beginJavaDocComment() {
            target.beginJavaDocComment();
            addChars(10);
        }

        private void addChars(int i) {
            approximateChars += i;
        }

        private void addChars(String s) {
            addChars(s.length());
        }

        private void addChars(String s, Object[] args) {
            addChars(String.format(s, args));
        }

        @Override
        public void commit(TreeLogger logger) {
            target.commit(logger);
        }

        @Override
        public void endJavaDocComment() {
            target.endJavaDocComment();
            addChars(10);
        }

        @Override
        public void indent() {
            target.indent();
            addChars(10);
        }

        @Override
        public void indentln(String s) {
            target.indentln(s);
            addChars(s);
        }

        @Override
        public void indentln(String s, Object... args) {
            target.indentln(s, args);
            addChars(s, args);
        }

        @Override
        public void outdent() {
            target.outdent();
        }

        @Override
        public void print(String s) {
            target.print(s);
            addChars(s);
        }

        @Override
        public void print(String s, Object... args) {
            target.print(s, args);
            addChars(s, args);
        }

        @Override
        public void println() {
            target.println();
            addChars(5);
        }

        @Override
        public void println(String s) {
            target.println(s);
            addChars(s);
        }

        @Override
        public void println(String s, Object... args) {
            target.println(s, args);
            addChars(s, args);
        }

        public void splitIfNeeded() {
            if (approximateChars > splitSize) {
                String newMethod = baseName + wrapCount++;
                println("%s();", newMethod);
                outdent();
                println("}");
                println("private void %s() {", newMethod);
                indent();

                approximateChars = 0;
            }
        }
    }

    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
            String typeName) throws UnableToCompleteException {
        TypeOracle typeOracle = context.getTypeOracle();

        try {
            JClassType classType = typeOracle.getType(typeName);
            String packageName = classType.getPackage().getName();
            String className = classType.getSimpleSourceName() + "Impl";

            generateClass(logger, context, packageName, className, typeName);

            return packageName + "." + className;
        } catch (UnableToCompleteException e) {
            // Just rethrow
            throw e;
        } catch (Exception e) {
            logger.log(Type.ERROR, getClass() + " failed", e);
            throw new UnableToCompleteException();
        }

    }

    private void generateClass(TreeLogger logger, GeneratorContext context,
            String packageName, String className, String requestedType)
            throws Exception {
        PrintWriter printWriter = context.tryCreate(logger, packageName,
                className);
        if (printWriter == null) {
            return;
        }

        List<ConnectorBundle> bundles = buildBundles(logger,
                context.getTypeOracle());

        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(
                packageName, className);
        composer.setSuperclass(requestedType);

        SourceWriter w = composer.createSourceWriter(context, printWriter);

        w.println("public void init() {");
        w.indent();

        for (ConnectorBundle bundle : bundles) {
            String name = bundle.getName();
            boolean isEager = name
                    .equals(ConnectorBundleLoader.EAGER_BUNDLE_NAME);

            w.print("addAsyncBlockLoader(new AsyncBundleLoader(\"");
            w.print(escape(name));
            w.print("\", ");

            w.print("new String[] {");
            for (Entry<JClassType, Set<String>> entry : bundle.getIdentifiers()
                    .entrySet()) {
                Set<String> identifiers = entry.getValue();
                for (String id : identifiers) {
                    w.print("\"");
                    w.print(escape(id));
                    w.print("\",");
                }
            }
            w.println("}) {");
            w.indent();

            w.print("protected void load(final ");
            w.print(TypeDataStore.class.getName());
            w.println(" store) {");
            w.indent();

            if (!isEager) {
                w.print(GWT.class.getName());
                w.print(".runAsync(");
            }

            w.println("new %s() {", RunAsyncCallback.class.getName());
            w.indent();

            w.println("public void onSuccess() {");
            w.indent();

            w.println("load();");
            w.println("%s.get().setLoaded(getName());",
                    ConnectorBundleLoader.class.getName());

            // Close onSuccess method
            w.outdent();
            w.println("}");

            w.println("private void load() {");
            w.indent();

            printBundleData(logger, w, bundle);

            // Close load method
            w.outdent();
            w.println("}");

            w.println("public void onFailure(Throwable reason) {");
            w.indent();

            w.println("%s.get().setLoadFailure(getName(), reason);",
                    ConnectorBundleLoader.class.getName());

            w.outdent();
            w.println("}");

            // Close new RunAsyncCallback() {}
            w.outdent();
            w.print("}");

            if (isEager) {
                w.println(".onSuccess();");
            } else {
                w.println(");");
            }

            // Close load method
            w.outdent();
            w.println("}");

            // Close add(new ...
            w.outdent();
            w.println("});");
        }

        w.outdent();
        w.println("}");

        w.commit(logger);
    }

    private void printBundleData(TreeLogger logger, SourceWriter sourceWriter,
            ConnectorBundle bundle) throws UnableToCompleteException {
        // Split into new load method when reaching approximately 30000 bytes
        SplittingSourceWriter w = new SplittingSourceWriter(sourceWriter,
                "load", 30000);

        writeIdentifiers(w, bundle);
        writeGwtConstructors(w, bundle);
        writeReturnTypes(w, bundle);
        writeInvokers(w, bundle);
        writeParamTypes(w, bundle);
        writeProxys(w, bundle);
        wirteDelayedInfo(w, bundle);
        writeProperites(logger, w, bundle);
        writePropertyTypes(w, bundle);
        writeSetters(logger, w, bundle);
        writeGetters(logger, w, bundle);
        writeSerializers(logger, w, bundle);
        writeDelegateToWidget(logger, w, bundle);
    }

    private void writeDelegateToWidget(TreeLogger logger,
            SplittingSourceWriter w, ConnectorBundle bundle) {
        Set<Property> needsDelegateToWidget = bundle.getNeedsDelegateToWidget();
        for (Property property : needsDelegateToWidget) {
            w.println("store.setDelegateToWidget(%s, \"%s\", \"%s\");",
                    getClassLiteralString(property.getBeanType()),
                    property.getName(),
                    property.getAnnotation(DelegateToWidget.class).value());

            w.splitIfNeeded();
        }
    }

    private void writeSerializers(TreeLogger logger, SplittingSourceWriter w,
            ConnectorBundle bundle) throws UnableToCompleteException {
        Map<JType, GeneratedSerializer> serializers = bundle.getSerializers();
        for (Entry<JType, GeneratedSerializer> entry : serializers.entrySet()) {
            JType type = entry.getKey();
            GeneratedSerializer serializer = entry.getValue();

            w.print("store.setSerializerFactory(");
            writeClassLiteral(w, type);
            w.print(", ");
            w.println("new Invoker() {");
            w.indent();

            w.println("public Object invoke(Object target, Object[] params) {");
            w.indent();

            serializer.writeSerializerInstantiator(logger, w);

            w.outdent();
            w.println("}");

            w.outdent();
            w.print("}");
            w.println(");");

            w.splitIfNeeded();
        }
    }

    private void writeGetters(TreeLogger logger, SplittingSourceWriter w,
            ConnectorBundle bundle) {
        Set<Property> properties = bundle.getNeedsSetter();
        for (Property property : properties) {
            w.print("store.setGetter(");
            writeClassLiteral(w, property.getBeanType());
            w.print(", \"");
            w.print(escape(property.getName()));
            w.println("\", new Invoker() {");
            w.indent();

            w.println("public Object invoke(Object bean, Object[] params) {");
            w.indent();

            property.writeGetterBody(logger, w, "bean");
            w.println();

            w.outdent();
            w.println("}");

            w.outdent();
            w.println("});");

            w.splitIfNeeded();
        }
    }

    private void writeSetters(TreeLogger logger, SplittingSourceWriter w,
            ConnectorBundle bundle) {
        Set<Property> properties = bundle.getNeedsSetter();
        for (Property property : properties) {
            w.print("store.setSetter(");
            writeClassLiteral(w, property.getBeanType());
            w.print(", \"");
            w.print(escape(property.getName()));
            w.println("\", new Invoker() {");
            w.indent();

            w.println("public Object invoke(Object bean, Object[] params) {");
            w.indent();

            property.writeSetterBody(logger, w, "bean", "params[0]");

            w.println("return null;");

            w.outdent();
            w.println("}");

            w.outdent();
            w.println("});");

            w.splitIfNeeded();
        }
    }

    private void writePropertyTypes(SplittingSourceWriter w,
            ConnectorBundle bundle) {
        Set<Property> properties = bundle.getNeedsType();
        for (Property property : properties) {
            w.print("store.setPropertyType(");
            writeClassLiteral(w, property.getBeanType());
            w.print(", \"");
            w.print(escape(property.getName()));
            w.print("\", ");
            writeTypeCreator(w, property.getPropertyType());
            w.println(");");

            w.splitIfNeeded();
        }
    }

    private void writeProperites(TreeLogger logger, SplittingSourceWriter w,
            ConnectorBundle bundle) throws UnableToCompleteException {
        Set<JClassType> needsPropertyListing = bundle.getNeedsPropertyListing();
        for (JClassType type : needsPropertyListing) {
            w.print("store.setProperties(");
            writeClassLiteral(w, type);
            w.print(", new String[] {");

            Set<String> usedPropertyNames = new HashSet<String>();
            Collection<Property> properties = bundle.getProperties(type);
            for (Property property : properties) {
                String name = property.getName();
                if (!usedPropertyNames.add(name)) {
                    logger.log(
                            Type.ERROR,
                            type.getQualifiedSourceName()
                                    + " has multiple properties with the name "
                                    + name
                                    + ". This can happen if there are multiple setters with identical names exect casing.");
                    throw new UnableToCompleteException();
                }

                w.print("\"");
                w.print(name);
                w.print("\", ");
            }

            w.println("});");

            w.splitIfNeeded();
        }
    }

    private void wirteDelayedInfo(SplittingSourceWriter w,
            ConnectorBundle bundle) {
        Map<JClassType, Set<JMethod>> needsDelayedInfo = bundle
                .getNeedsDelayedInfo();
        Set<Entry<JClassType, Set<JMethod>>> entrySet = needsDelayedInfo
                .entrySet();
        for (Entry<JClassType, Set<JMethod>> entry : entrySet) {
            JClassType type = entry.getKey();
            Set<JMethod> methods = entry.getValue();
            for (JMethod method : methods) {
                Delayed annotation = method.getAnnotation(Delayed.class);
                if (annotation != null) {
                    w.print("store.setDelayed(");
                    writeClassLiteral(w, type);
                    w.print(", \"");
                    w.print(escape(method.getName()));
                    w.println("\");");

                    if (annotation.lastOnly()) {
                        w.print("store.setLastOnly(");
                        writeClassLiteral(w, type);
                        w.print(", \"");
                        w.print(escape(method.getName()));
                        w.println("\");");
                    }

                    w.splitIfNeeded();
                }
            }
        }
    }

    private void writeProxys(SplittingSourceWriter w, ConnectorBundle bundle) {
        Set<JClassType> needsProxySupport = bundle.getNeedsProxySupport();
        for (JClassType type : needsProxySupport) {
            w.print("store.setProxyHandler(");
            writeClassLiteral(w, type);
            w.print(", new ");
            w.print(ProxyHandler.class.getCanonicalName());
            w.println("() {");
            w.indent();

            w.println("public Object createProxy(final "
                    + InvokationHandler.class.getName() + " handler) {");
            w.indent();

            w.print("return new ");
            w.print(type.getQualifiedSourceName());
            w.println("() {");
            w.indent();

            JMethod[] methods = type.getOverridableMethods();
            for (JMethod method : methods) {
                if (method.isAbstract()) {
                    w.print("public ");
                    w.print(method.getReturnType().getQualifiedSourceName());
                    w.print(" ");
                    w.print(method.getName());
                    w.print("(");

                    JType[] types = method.getParameterTypes();
                    for (int i = 0; i < types.length; i++) {
                        if (i != 0) {
                            w.print(", ");
                        }
                        w.print(types[i].getQualifiedSourceName());
                        w.print(" p");
                        w.print(Integer.toString(i));
                    }

                    w.println(") {");
                    w.indent();

                    if (!method.getReturnType().getQualifiedSourceName()
                            .equals("void")) {
                        w.print("return ");
                    }

                    w.print("handler.invoke(this, ");
                    w.print(TypeData.class.getCanonicalName());
                    w.print(".getType(");
                    writeClassLiteral(w, type);
                    w.print(").getMethod(\"");
                    w.print(escape(method.getName()));
                    w.print("\"), new Object [] {");
                    for (int i = 0; i < types.length; i++) {
                        w.print("p" + i + ", ");
                    }
                    w.println("});");

                    w.outdent();
                    w.println("}");
                }
            }

            w.outdent();
            w.println("};");

            w.outdent();
            w.println("}");

            w.outdent();
            w.println("});");

            w.splitIfNeeded();
        }
    }

    private void writeParamTypes(SplittingSourceWriter w, ConnectorBundle bundle) {
        Map<JClassType, Set<JMethod>> needsParamTypes = bundle
                .getNeedsParamTypes();
        for (Entry<JClassType, Set<JMethod>> entry : needsParamTypes.entrySet()) {
            JClassType type = entry.getKey();

            Set<JMethod> methods = entry.getValue();
            for (JMethod method : methods) {
                w.print("store.setParamTypes(");
                writeClassLiteral(w, type);
                w.print(", \"");
                w.print(escape(method.getName()));
                w.print("\", new Type[] {");

                for (JType parameter : method.getParameterTypes()) {
                    ConnectorBundleLoaderFactory.writeTypeCreator(w, parameter);
                    w.print(", ");
                }

                w.println("});");

                w.splitIfNeeded();
            }
        }
    }

    private void writeInvokers(SplittingSourceWriter w, ConnectorBundle bundle) {
        Map<JClassType, Set<JMethod>> needsInvoker = bundle.getNeedsInvoker();
        for (Entry<JClassType, Set<JMethod>> entry : needsInvoker.entrySet()) {
            JClassType type = entry.getKey();

            Set<JMethod> methods = entry.getValue();
            for (JMethod method : methods) {
                w.print("store.setInvoker(");
                writeClassLiteral(w, type);
                w.print(", \"");
                w.print(escape(method.getName()));
                w.println("\", new Invoker() {");
                w.indent();

                w.println("public Object invoke(Object target, Object[] params) {");
                w.indent();

                JType returnType = method.getReturnType();
                boolean hasReturnType = !"void".equals(returnType
                        .getQualifiedSourceName());
                if (hasReturnType) {
                    w.print("return ");
                }

                JType[] parameterTypes = method.getParameterTypes();

                w.print("((" + type.getQualifiedSourceName() + ") target)."
                        + method.getName() + "(");
                for (int i = 0; i < parameterTypes.length; i++) {
                    JType parameterType = parameterTypes[i];
                    if (i != 0) {
                        w.print(", ");
                    }
                    String parameterTypeName = getBoxedTypeName(parameterType);
                    w.print("(" + parameterTypeName + ") params[" + i + "]");
                }
                w.println(");");

                if (!hasReturnType) {
                    w.println("return null;");
                }

                w.outdent();
                w.println("}");

                w.outdent();
                w.println("});");

                w.splitIfNeeded();
            }
        }
    }

    private void writeReturnTypes(SplittingSourceWriter w,
            ConnectorBundle bundle) {
        Map<JClassType, Set<JMethod>> methodReturnTypes = bundle
                .getMethodReturnTypes();
        for (Entry<JClassType, Set<JMethod>> entry : methodReturnTypes
                .entrySet()) {
            JClassType type = entry.getKey();

            Set<JMethod> methods = entry.getValue();
            for (JMethod method : methods) {
                // setReturnType(Class<?> type, String methodName, Type
                // returnType)
                w.print("store.setReturnType(");
                writeClassLiteral(w, type);
                w.print(", \"");
                w.print(escape(method.getName()));
                w.print("\", ");
                writeTypeCreator(w, method.getReturnType());
                w.println(");");

                w.splitIfNeeded();
            }
        }
    }

    private void writeGwtConstructors(SplittingSourceWriter w,
            ConnectorBundle bundle) {
        Set<JClassType> constructors = bundle.getGwtConstructors();
        for (JClassType type : constructors) {
            w.print("store.setConstructor(");
            writeClassLiteral(w, type);
            w.println(", new Invoker() {");
            w.indent();

            w.println("public Object invoke(Object target, Object[] params) {");
            w.indent();

            w.print("return ");
            w.print(GWT.class.getName());
            w.print(".create(");
            writeClassLiteral(w, type);
            w.println(");");

            w.outdent();
            w.println("}");

            w.outdent();
            w.println("});");

            w.splitIfNeeded();
        }
    }

    public static void writeClassLiteral(SourceWriter w, JType type) {
        w.print(getClassLiteralString(type));
    }

    public static String getClassLiteralString(JType type) {
        return type.getQualifiedSourceName() + ".class";
    }

    private void writeIdentifiers(SplittingSourceWriter w,
            ConnectorBundle bundle) {
        Map<JClassType, Set<String>> identifiers = bundle.getIdentifiers();
        for (Entry<JClassType, Set<String>> entry : identifiers.entrySet()) {
            Set<String> ids = entry.getValue();
            JClassType type = entry.getKey();
            for (String id : ids) {
                w.print("store.setClass(\"");
                w.print(escape(id));
                w.print("\", ");
                writeClassLiteral(w, type);
                w.println(");");

                w.splitIfNeeded();
            }
        }
    }

    private List<ConnectorBundle> buildBundles(TreeLogger logger,
            TypeOracle typeOracle) throws NotFoundException,
            UnableToCompleteException {

        Map<LoadStyle, Collection<JClassType>> connectorsByLoadStyle = new HashMap<LoadStyle, Collection<JClassType>>();
        for (LoadStyle loadStyle : LoadStyle.values()) {
            connectorsByLoadStyle.put(loadStyle, new ArrayList<JClassType>());
        }

        // Find all types with a valid mapping
        Collection<JClassType> selectedTypes = getConnectorsForWidgetset(
                logger, typeOracle);

        // Group by load style
        for (JClassType connectorSubtype : selectedTypes) {
            LoadStyle loadStyle = getLoadStyle(connectorSubtype);
            if (loadStyle != null) {
                connectorsByLoadStyle.get(loadStyle).add(connectorSubtype);
            }
        }

        List<ConnectorBundle> bundles = new ArrayList<ConnectorBundle>();

        Collection<TypeVisitor> visitors = getVisitors(typeOracle);

        ConnectorBundle eagerBundle = new ConnectorBundle(
                ConnectorBundleLoader.EAGER_BUNDLE_NAME, visitors, typeOracle);
        TreeLogger eagerLogger = logger.branch(Type.TRACE,
                "Populating eager bundle");

        // Eager connectors and all RPC interfaces are loaded by default
        eagerBundle.processTypes(eagerLogger,
                connectorsByLoadStyle.get(LoadStyle.EAGER));
        eagerBundle.processType(eagerLogger, typeOracle
                .findType(UnknownComponentConnector.class.getCanonicalName()));
        eagerBundle.processSubTypes(eagerLogger,
                typeOracle.getType(ClientRpc.class.getName()));
        eagerBundle.processSubTypes(eagerLogger,
                typeOracle.getType(ServerRpc.class.getName()));

        bundles.add(eagerBundle);

        ConnectorBundle deferredBundle = new ConnectorBundle(
                ConnectorBundleLoader.DEFERRED_BUNDLE_NAME, eagerBundle);
        TreeLogger deferredLogger = logger.branch(Type.TRACE,
                "Populating deferred bundle");
        deferredBundle.processTypes(deferredLogger,
                connectorsByLoadStyle.get(LoadStyle.DEFERRED));

        bundles.add(deferredBundle);

        Collection<JClassType> lazy = connectorsByLoadStyle.get(LoadStyle.LAZY);
        for (JClassType type : lazy) {
            ConnectorBundle bundle = new ConnectorBundle(type.getName(),
                    eagerBundle);
            TreeLogger subLogger = logger.branch(Type.TRACE, "Populating "
                    + type.getName() + " bundle");
            bundle.processType(subLogger, type);

            bundles.add(bundle);
        }

        return bundles;
    }

    /**
     * Returns the connector types that should be included in the widgetset.
     * This method can be overridden to create a widgetset only containing
     * selected connectors.
     * <p>
     * The default implementation finds all type implementing
     * {@link ServerConnector} that have a @{@link Connect} annotation. It also
     * checks that multiple connectors aren't connected to the same server-side
     * class.
     * 
     * @param logger
     *            the logger to which information can be logged
     * @param typeOracle
     *            the type oracle that can be used for finding types
     * @return a collection of all the connector types that should be included
     *         in the widgetset
     * @throws UnableToCompleteException
     *             if the operation fails
     */
    protected Collection<JClassType> getConnectorsForWidgetset(
            TreeLogger logger, TypeOracle typeOracle)
            throws UnableToCompleteException {
        JClassType serverConnectorType;
        try {
            serverConnectorType = typeOracle.getType(ServerConnector.class
                    .getName());
        } catch (NotFoundException e) {
            logger.log(Type.ERROR,
                    "Can't find " + ServerConnector.class.getName());
            throw new UnableToCompleteException();
        }

        JClassType[] types = serverConnectorType.getSubtypes();

        Map<String, JClassType> mappings = new HashMap<String, JClassType>();

        // Keep track of what has happened to avoid logging intermediate state
        Map<JClassType, List<JClassType>> replaced = new HashMap<JClassType, List<JClassType>>();

        for (JClassType type : types) {
            Connect connectAnnotation = type.getAnnotation(Connect.class);
            if (connectAnnotation == null) {
                continue;
            }

            String identifier = connectAnnotation.value().getCanonicalName();

            JClassType previousMapping = mappings.put(identifier, type);
            if (previousMapping != null) {
                // There are multiple mappings, pick the subclass
                JClassType subclass;
                JClassType superclass;
                if (previousMapping.isAssignableFrom(type)) {
                    subclass = type;
                    superclass = previousMapping;
                } else if (type.isAssignableFrom(previousMapping)) {
                    subclass = previousMapping;
                    superclass = type;
                } else {
                    // Neither inherits from the other - this is a conflict
                    logger.log(
                            Type.ERROR,
                            "Conflicting @Connect mappings detected for "
                                    + identifier
                                    + ": "
                                    + type.getQualifiedSourceName()
                                    + " and "
                                    + previousMapping.getQualifiedSourceName()
                                    + ". There can only be multiple @Connect mappings for the same server-side type if one is the subclass of the other.");
                    throw new UnableToCompleteException();
                }

                mappings.put(identifier, subclass);

                // Inherit any previous replacements
                List<JClassType> previousReplacements = replaced
                        .remove(superclass);
                if (previousReplacements == null) {
                    previousReplacements = new ArrayList<JClassType>();
                }

                previousReplacements.add(superclass);
                replaced.put(subclass, previousReplacements);
            }
        }

        // Log the final set of replacements
        for (Entry<JClassType, List<JClassType>> entry : replaced.entrySet()) {
            String msg = entry.getKey().getQualifiedSourceName() + " replaces ";

            List<JClassType> list = entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                if (i != 0) {
                    msg += ", ";
                }
                msg += list.get(i).getQualifiedSourceName();
            }

            logger.log(Type.INFO, msg);
        }

        // Return the types of the final mapping
        return mappings.values();
    }

    private Collection<TypeVisitor> getVisitors(TypeOracle oracle)
            throws NotFoundException {
        List<TypeVisitor> visitors = Arrays.<TypeVisitor> asList(
                new ConnectorInitVisitor(), new StateInitVisitor(),
                new WidgetInitVisitor(), new ClientRpcVisitor(),
                new ServerRpcVisitor());
        for (TypeVisitor typeVisitor : visitors) {
            typeVisitor.init(oracle);
        }
        return visitors;
    }

    protected LoadStyle getLoadStyle(JClassType connectorType) {
        Connect annotation = connectorType.getAnnotation(Connect.class);
        return annotation.loadStyle();
    }

    public static String getBoxedTypeName(JType type) {
        if (type.isPrimitive() != null) {
            // Used boxed types for primitives
            return type.isPrimitive().getQualifiedBoxedSourceName();
        } else {
            return type.getErasedType().getQualifiedSourceName();
        }
    }

    public static void writeTypeCreator(SourceWriter sourceWriter, JType type) {
        String typeName = ConnectorBundleLoaderFactory.getBoxedTypeName(type);
        JParameterizedType parameterized = type.isParameterized();
        if (parameterized != null) {
            sourceWriter.print("new Type(\"" + typeName + "\", ");
            sourceWriter.print("new Type[] {");
            JClassType[] typeArgs = parameterized.getTypeArgs();
            for (JClassType jClassType : typeArgs) {
                writeTypeCreator(sourceWriter, jClassType);
                sourceWriter.print(", ");
            }
            sourceWriter.print("}");
        } else {
            sourceWriter.print("new Type(" + typeName + ".class");
        }
        sourceWriter.print(")");
    }

}
