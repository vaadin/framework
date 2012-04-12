/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.Connect.LoadStyle;
import com.vaadin.terminal.gwt.client.ui.UnknownComponentConnector;
import com.vaadin.terminal.gwt.client.ui.root.RootConnector;
import com.vaadin.terminal.gwt.server.ClientConnector;

/**
 * WidgetMapGenerator's are GWT generator to build WidgetMapImpl dynamically
 * based on {@link Connect} annotations available in workspace. By modifying the
 * generator it is possible to do some fine tuning for the generated widgetset
 * (aka client side engine). The components to be included in the client side
 * engine can modified be overriding {@link #getUsedConnectors()}.
 * <p>
 * The generator also decides how the client side component implementations are
 * loaded to the browser. The default generator is
 * {@link EagerWidgetMapGenerator} that builds a monolithic client side engine
 * that loads all widget implementation on application initialization. This has
 * been the only option until Vaadin 6.4.
 * <p>
 * This generator uses the loadStyle hints from the {@link Connect} annotations.
 * Depending on the {@link LoadStyle} used, the widget may be included in the
 * initially loaded JavaScript, loaded when the application has started and
 * there is no communication to server or lazy loaded when the implementation is
 * absolutely needed.
 * <p>
 * The GWT module description file of the widgetset (
 * <code>...Widgetset.gwt.xml</code>) can be used to define the
 * WidgetMapGenarator. An example that defines this generator to be used:
 * 
 * <pre>
 * <code>
 * &lt;generate-with
 *           class="com.vaadin.terminal.gwt.widgetsetutils.MyWidgetMapGenerator"&gt;
 *          &lt;when-type-is class="com.vaadin.terminal.gwt.client.WidgetMap" /&gt;
 * &lt;/generate-with&gt;
 * 
 * </code>
 * </pre>
 * 
 * <p>
 * Vaadin package also includes {@link LazyWidgetMapGenerator}, which is a good
 * option if the transferred data should be minimized, and
 * {@link CustomWidgetMapGenerator} for easy overriding of loading strategies.
 * 
 */
public class WidgetMapGenerator extends Generator {

    private static String componentConnectorClassName = ComponentConnector.class
            .getName();

    private String packageName;
    private String className;

    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
            String typeName) throws UnableToCompleteException {

        try {
            TypeOracle typeOracle = context.getTypeOracle();

            // get classType and save instance variables
            JClassType classType = typeOracle.getType(typeName);
            packageName = classType.getPackage().getName();
            className = classType.getSimpleSourceName() + "Impl";
            // Generate class source code
            generateClass(logger, context);
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR, "WidgetMap creation failed", e);
        }
        // return the fully qualifed name of the class generated
        return packageName + "." + className;
    }

    /**
     * Generate source code for WidgetMapImpl
     * 
     * @param logger
     *            Logger object
     * @param context
     *            Generator context
     */
    private void generateClass(TreeLogger logger, GeneratorContext context) {
        // get print writer that receives the source code
        PrintWriter printWriter = null;
        printWriter = context.tryCreate(logger, packageName, className);
        // print writer if null, source code has ALREADY been generated,
        // return (WidgetMap is equal to all permutations atm)
        if (printWriter == null) {
            return;
        }
        logger.log(Type.INFO,
                "Detecting Vaadin components in classpath to generate WidgetMapImpl.java ...");
        Date date = new Date();

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport("com.google.gwt.core.client.GWT");
        composer.addImport("java.util.HashMap");
        composer.addImport("com.google.gwt.core.client.RunAsyncCallback");
        composer.setSuperclass("com.vaadin.terminal.gwt.client.WidgetMap");
        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);

        Collection<Class<? extends ComponentConnector>> connectors = getUsedConnectors(context
                .getTypeOracle());

        validateConnectors(logger, connectors);
        logConnectors(logger, context, connectors);

        // generator constructor source code
        generateImplementationDetector(sourceWriter, connectors);
        generateInstantiatorMethod(sourceWriter, connectors);
        // close generated class
        sourceWriter.outdent();
        sourceWriter.println("}");
        // commit generated class
        context.commit(logger, printWriter);
        logger.log(Type.INFO,
                "Done. (" + (new Date().getTime() - date.getTime()) / 1000
                        + "seconds)");

    }

    private void validateConnectors(TreeLogger logger,
            Collection<Class<? extends ComponentConnector>> connectors) {

        Iterator<Class<? extends ComponentConnector>> iter = connectors
                .iterator();
        while (iter.hasNext()) {
            Class<? extends ComponentConnector> connectorClass = iter.next();
            Connect annotation = connectorClass.getAnnotation(Connect.class);
            if (!ClientConnector.class.isAssignableFrom(annotation.value())) {
                logger.log(
                        Type.WARN,
                        "Connector class "
                                + annotation.value().getName()
                                + " defined in @Connect annotation is not a subclass of "
                                + ClientConnector.class.getName()
                                + ". The component connector "
                                + connectorClass.getName()
                                + " will not be included in the widgetset.");
                iter.remove();
            }
        }

    }

    private void logConnectors(TreeLogger logger, GeneratorContext context,
            Collection<Class<? extends ComponentConnector>> connectors) {
        logger.log(Type.INFO,
                "Widget set will contain implementations for following component connectors: ");

        TreeSet<String> classNames = new TreeSet<String>();
        HashMap<String, String> loadStyle = new HashMap<String, String>();
        for (Class<? extends ComponentConnector> connectorClass : connectors) {
            String className = connectorClass.getCanonicalName();
            classNames.add(className);
            if (getLoadStyle(connectorClass) == LoadStyle.DEFERRED) {
                loadStyle.put(className, "DEFERRED");
            } else if (getLoadStyle(connectorClass) == LoadStyle.LAZY) {
                loadStyle.put(className, "LAZY");
            }

        }
        for (String className : classNames) {
            String msg = className;
            if (loadStyle.containsKey(className)) {
                msg += " (load style: " + loadStyle.get(className) + ")";
            }
            logger.log(Type.INFO, "\t" + msg);
        }
    }

    /**
     * This method is protected to allow creation of optimized widgetsets. The
     * Widgetset will contain only implementation returned by this function. If
     * one knows which widgets are needed for the application, returning only
     * them here will significantly optimize the size of the produced JS.
     * 
     * @return a collections of Vaadin components that will be added to
     *         widgetset
     */
    @SuppressWarnings("unchecked")
    private Collection<Class<? extends ComponentConnector>> getUsedConnectors(
            TypeOracle typeOracle) {
        JClassType connectorType = typeOracle.findType(Connector.class
                .getName());
        Collection<Class<? extends ComponentConnector>> connectors = new HashSet<Class<? extends ComponentConnector>>();
        for (JClassType jClassType : connectorType.getSubtypes()) {
            Connect annotation = jClassType.getAnnotation(Connect.class);
            if (annotation != null) {
                try {
                    Class<? extends ComponentConnector> clazz = (Class<? extends ComponentConnector>) Class
                            .forName(jClassType.getQualifiedSourceName());
                    connectors.add(clazz);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return connectors;
    }

    /**
     * Returns true if the widget for given component will be lazy loaded by the
     * client. The default implementation reads the information from the
     * {@link Connect} annotation.
     * <p>
     * The method can be overridden to optimize the widget loading mechanism. If
     * the Widgetset is wanted to be optimized for a network with a high latency
     * or for a one with a very fast throughput, it may be good to return false
     * for every component.
     * 
     * @param connector
     * @return true iff the widget for given component should be lazy loaded by
     *         the client side engine
     */
    protected LoadStyle getLoadStyle(
            Class<? extends ComponentConnector> connector) {
        Connect annotation = connector.getAnnotation(Connect.class);
        return annotation.loadStyle();
    }

    private void generateInstantiatorMethod(
            SourceWriter sourceWriter,
            Collection<Class<? extends ComponentConnector>> connectorsHavingComponentAnnotation) {

        Collection<Class<?>> deferredWidgets = new LinkedList<Class<?>>();

        // TODO detect if it would be noticably faster to instantiate with a
        // lookup with index than with the hashmap

        sourceWriter.println("public void ensureInstantiator(Class<? extends "
                + componentConnectorClassName + "> classType) {");
        sourceWriter.println("if(!instmap.containsKey(classType)){");
        boolean first = true;

        ArrayList<Class<? extends ComponentConnector>> lazyLoadedWidgets = new ArrayList<Class<? extends ComponentConnector>>();

        HashSet<Class<? extends com.vaadin.terminal.gwt.client.ComponentConnector>> connectorsWithInstantiator = new HashSet<Class<? extends com.vaadin.terminal.gwt.client.ComponentConnector>>();

        for (Class<? extends ComponentConnector> class1 : connectorsHavingComponentAnnotation) {
            Class<? extends ComponentConnector> clientClass = class1;
            if (connectorsWithInstantiator.contains(clientClass)) {
                continue;
            }
            if (clientClass == RootConnector.class) {
                // Roots are not instantiated by widgetset
                continue;
            }
            if (!first) {
                sourceWriter.print(" else ");
            } else {
                first = false;
            }
            sourceWriter.print("if( classType == " + clientClass.getName()
                    + ".class) {");

            String instantiator = "new WidgetInstantiator() {\n public "
                    + componentConnectorClassName
                    + " get() {\n return GWT.create(" + clientClass.getName()
                    + ".class );\n}\n}\n";

            LoadStyle loadStyle = getLoadStyle(class1);

            if (loadStyle != LoadStyle.EAGER) {
                sourceWriter
                        .print("ApplicationConfiguration.startWidgetLoading();\n"
                                + "GWT.runAsync( \n"
                                + "new WidgetLoader() { void addInstantiator() {instmap.put("
                                + clientClass.getName()
                                + ".class,"
                                + instantiator + ");}});\n");
                lazyLoadedWidgets.add(class1);

                if (loadStyle == LoadStyle.DEFERRED) {
                    deferredWidgets.add(class1);
                }

            } else {
                // widget implementation in initially loaded js script
                sourceWriter.print("instmap.put(");
                sourceWriter.print(clientClass.getName());
                sourceWriter.print(".class, ");
                sourceWriter.print(instantiator);
                sourceWriter.print(");");
            }
            sourceWriter.print("}");
            connectorsWithInstantiator.add(clientClass);
        }

        sourceWriter.println("}");

        sourceWriter.println("}");

        sourceWriter.println("public Class<? extends "
                + componentConnectorClassName
                + ">[] getDeferredLoadedWidgets() {");

        sourceWriter.println("return new Class[] {");
        first = true;
        for (Class<?> class2 : deferredWidgets) {
            if (!first) {
                sourceWriter.println(",");
            }
            first = false;
            sourceWriter.print(class2.getName() + ".class");
        }

        sourceWriter.println("};");
        sourceWriter.println("}");

        // in constructor add a "thread" that lazyly loads lazy loaded widgets
        // if communication to server idles

        // TODO an array of lazy loaded widgets

        // TODO an index of last ensured widget in array

        sourceWriter.println("public " + componentConnectorClassName
                + " instantiate(Class<? extends " + componentConnectorClassName
                + "> classType) {");
        sourceWriter.indent();
        sourceWriter.println(componentConnectorClassName
                + " p = super.instantiate(classType); if(p!= null) return p;");
        sourceWriter.println("return instmap.get(classType).get();");

        sourceWriter.outdent();
        sourceWriter.println("}");

    }

    /**
     * 
     * @param sourceWriter
     *            Source writer to output source code
     * @param paintablesHavingWidgetAnnotation
     */
    private void generateImplementationDetector(
            SourceWriter sourceWriter,
            Collection<Class<? extends ComponentConnector>> paintablesHavingWidgetAnnotation) {
        sourceWriter
                .println("public Class<? extends "
                        + componentConnectorClassName
                        + "> "
                        + "getConnectorClassForServerSideClassName(String fullyQualifiedName) {");
        sourceWriter.indent();
        sourceWriter
                .println("fullyQualifiedName = fullyQualifiedName.intern();");

        for (Class<? extends ComponentConnector> connectorClass : paintablesHavingWidgetAnnotation) {
            Class<? extends ClientConnector> clientConnectorClass = getClientConnectorClass(connectorClass);
            sourceWriter.print("if ( fullyQualifiedName == \"");
            sourceWriter.print(clientConnectorClass.getName());
            sourceWriter.print("\" ) { ensureInstantiator("
                    + connectorClass.getName() + ".class); return ");
            sourceWriter.print(connectorClass.getName());
            sourceWriter.println(".class;}");
            sourceWriter.print("else ");
        }
        sourceWriter.println("return "
                + UnknownComponentConnector.class.getName() + ".class;");
        sourceWriter.outdent();
        sourceWriter.println("}");

    }

    private static Class<? extends ClientConnector> getClientConnectorClass(
            Class<? extends ComponentConnector> connectorClass) {
        Connect annotation = connectorClass.getAnnotation(Connect.class);
        return (Class<? extends ClientConnector>) annotation.value();
    }
}
