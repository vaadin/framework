/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.client.metadata.ConnectorBundleLoader;
import com.vaadin.terminal.gwt.client.metadata.TypeDataBundle;
import com.vaadin.terminal.gwt.client.metadata.TypeDataStore;
import com.vaadin.terminal.gwt.widgetsetutils.metadata.ConnectorBundle;
import com.vaadin.terminal.gwt.widgetsetutils.metadata.ConnectorInitVisitor;
import com.vaadin.terminal.gwt.widgetsetutils.metadata.StateInitVisitor;
import com.vaadin.terminal.gwt.widgetsetutils.metadata.TypeVisitor;
import com.vaadin.terminal.gwt.widgetsetutils.metadata.WidgetInitVisitor;

public class ConnectorBundleLoaderFactory extends Generator {

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

            w.print("new ");
            w.print(TypeDataBundle.class.getName());
            w.println("(getName()) {");
            w.indent();

            w.println("public void load() {");
            w.indent();

            printBundleData(w, bundle);

            // Close load method
            w.outdent();
            w.println("}");

            // Close new TypeDataBundle() {}
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

    private void printBundleData(SourceWriter w, ConnectorBundle bundle) {
        writeIdentifiers(w, bundle);
        writeGwtConstructors(w, bundle);
        writeReturnTypes(w, bundle);
    }

    private void writeReturnTypes(SourceWriter w, ConnectorBundle bundle) {
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
                printClassLiteral(w, type);
                w.print(", \"");
                w.print(method.getName());
                w.print("\", ");
                GeneratedRpcMethodProviderGenerator.writeTypeCreator(w,
                        method.getReturnType());
                w.println(");");
            }
        }
    }

    private void writeGwtConstructors(SourceWriter w, ConnectorBundle bundle) {
        Set<JClassType> constructors = bundle.getGwtConstructors();
        for (JClassType type : constructors) {
            w.print("store.setConstructor(");
            printClassLiteral(w, type);
            w.print(", new Invoker() {");
            w.indent();

            w.println("public Object invoke(Object target, Object[] params) {");
            w.indent();

            w.print("return ");
            w.print(GWT.class.getName());
            w.print(".create(");
            printClassLiteral(w, type);
            w.println(");");

            w.outdent();
            w.println("}");

            w.outdent();
            w.println("});");
        }
    }

    private void printClassLiteral(SourceWriter w, JClassType type) {
        w.print(type.getQualifiedSourceName());
        w.print(".class");
    }

    private void writeIdentifiers(SourceWriter w, ConnectorBundle bundle) {
        Map<JClassType, Set<String>> identifiers = bundle.getIdentifiers();
        for (Entry<JClassType, Set<String>> entry : identifiers.entrySet()) {
            Set<String> ids = entry.getValue();
            JClassType type = entry.getKey();
            for (String id : ids) {
                w.print("store.setClass(\"");
                w.print(escape(id));
                w.print("\", ");
                printClassLiteral(w, type);
                w.println(");");
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

        JClassType connectorType = typeOracle.getType(ServerConnector.class
                .getName());
        JClassType[] subtypes = connectorType.getSubtypes();
        for (JClassType connectorSubtype : subtypes) {
            if (!connectorSubtype.isAnnotationPresent(Connect.class)) {
                continue;
            }
            LoadStyle loadStyle = getLoadStyle(connectorSubtype);
            if (loadStyle != null) {
                connectorsByLoadStyle.get(loadStyle).add(connectorSubtype);
            }
        }

        List<ConnectorBundle> bundles = new ArrayList<ConnectorBundle>();

        Collection<TypeVisitor> visitors = getVisitors(typeOracle);

        ConnectorBundle eagerBundle = new ConnectorBundle(
                ConnectorBundleLoader.EAGER_BUNDLE_NAME, null);

        // Eager connectors and all RPC interfaces are loaded by default
        eagerBundle.visitTypes(logger,
                connectorsByLoadStyle.get(LoadStyle.EAGER), visitors);
        eagerBundle.visitSubTypes(logger,
                typeOracle.getType(ClientRpc.class.getName()), visitors);
        eagerBundle.visitSubTypes(logger,
                typeOracle.getType(ServerRpc.class.getName()), visitors);

        bundles.add(eagerBundle);

        ConnectorBundle deferredBundle = new ConnectorBundle(
                ConnectorBundleLoader.DEFERRED_BUNDLE_NAME, eagerBundle);
        deferredBundle.visitTypes(logger,
                connectorsByLoadStyle.get(LoadStyle.DEFERRED), visitors);

        bundles.add(deferredBundle);

        Collection<JClassType> lazy = connectorsByLoadStyle.get(LoadStyle.LAZY);
        for (JClassType type : lazy) {
            ConnectorBundle bundle = new ConnectorBundle(type.getName(),
                    deferredBundle);
            bundle.visitTypes(logger, Collections.singleton(type), visitors);

            bundles.add(bundle);
        }

        return bundles;
    }

    private Collection<TypeVisitor> getVisitors(TypeOracle oracle)
            throws NotFoundException {
        List<TypeVisitor> visitors = Arrays.<TypeVisitor> asList(
                new ConnectorInitVisitor(), new StateInitVisitor(),
                new WidgetInitVisitor());
        for (TypeVisitor typeVisitor : visitors) {
            typeVisitor.init(oracle);
        }
        return visitors;
    }

    protected LoadStyle getLoadStyle(JClassType connectorType) {
        Connect annotation = connectorType.getAnnotation(Connect.class);
        return annotation.loadStyle();
    }

}
