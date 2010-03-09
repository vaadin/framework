/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.gwt.client.ui.VView;
import com.vaadin.ui.ClientWidget;

/**
 * GWT generator to build WidgetMapImpl dynamically based on
 * {@link ClientWidget} annotations available in workspace.
 * 
 */
public class WidgetMapGenerator extends Generator {

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
        logger
                .log(Type.INFO,
                        "Detecting Vaadin components in classpath to generate WidgetMapImpl.java ...");
        Date date = new Date();

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport("com.google.gwt.core.client.GWT");
        composer.setSuperclass("com.vaadin.terminal.gwt.client.WidgetMap");
        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);

        Collection<Class<? extends Paintable>> paintablesHavingWidgetAnnotation = getUsedPaintables();

        validatePaintables(logger, context, paintablesHavingWidgetAnnotation);

        // generator constructor source code
        generateImplementationDetector(sourceWriter,
                paintablesHavingWidgetAnnotation);
        generateInstantiatorMethod(sourceWriter,
                paintablesHavingWidgetAnnotation);
        // close generated class
        sourceWriter.outdent();
        sourceWriter.println("}");
        // commit generated class
        context.commit(logger, printWriter);
        logger.log(Type.INFO, "Done. ("
                + (new Date().getTime() - date.getTime()) / 1000 + "seconds)");

    }

    /**
     * Verifies that all client side components are available for client side
     * GWT module.
     * 
     * @param logger
     * @param context
     * @param paintablesHavingWidgetAnnotation
     */
    private void validatePaintables(
            TreeLogger logger,
            GeneratorContext context,
            Collection<Class<? extends Paintable>> paintablesHavingWidgetAnnotation) {
        TypeOracle typeOracle = context.getTypeOracle();

        for (Iterator<Class<? extends Paintable>> iterator = paintablesHavingWidgetAnnotation
                .iterator(); iterator.hasNext();) {
            Class<? extends Paintable> class1 = iterator.next();

            ClientWidget annotation = class1.getAnnotation(ClientWidget.class);

            if (typeOracle.findType(annotation.value().getName()) == null) {
                // GWT widget not inherited
                logger.log(Type.WARN, "Widget class "
                        + annotation.value().getName()
                        + " was not found. The component " + class1.getName()
                        + " will not be included in the widgetset.");
                iterator.remove();
            }

        }
        logger
                .log(Type.INFO,
                        "Widget set will contain implementations for following components: ");

        TreeSet<String> classNames = new TreeSet<String>();
        for (Class<? extends Paintable> class1 : paintablesHavingWidgetAnnotation) {
            classNames.add(class1.getCanonicalName());
        }
        for (String string : classNames) {
            logger.log(Type.INFO, "\t" + string);
        }
    }

    /**
     * This method is protected to allow easy creation of optimized widgetsets.
     * <p>
     * TODO we need some sort of mechanism to easily exclude/include components
     * from widgetset. Properties in gwt.xml is one option. Now only possible by
     * extending this class, overriding getUsedPaintables() method and
     * redefining deferred binding rule.
     * 
     * @return a collections of Vaadin components that will be added to
     *         widgetset
     */
    protected Collection<Class<? extends Paintable>> getUsedPaintables() {
        return ClassPathExplorer.getPaintablesHavingWidgetAnnotation();
    }

    private void generateInstantiatorMethod(
            SourceWriter sourceWriter,
            Collection<Class<? extends Paintable>> paintablesHavingWidgetAnnotation) {
        sourceWriter
                .println("public Paintable instantiate(Class<? extends Paintable> classType) {");
        sourceWriter.indent();

        sourceWriter
                .println("Paintable p = super.instantiate(classType); if(p!= null) return p;");

        for (Class<? extends Paintable> class1 : paintablesHavingWidgetAnnotation) {
            ClientWidget annotation = class1.getAnnotation(ClientWidget.class);
            Class<? extends com.vaadin.terminal.gwt.client.Paintable> clientClass = annotation
                    .value();
            if (clientClass == VView.class) {
                // VView's are not instantiated by widgetset
                continue;
            }
            sourceWriter.print("if (");
            sourceWriter.print(clientClass.getName());
            sourceWriter.print(".class == classType) return GWT.create(");
            sourceWriter.print(clientClass.getName());
            sourceWriter.println(".class );");
            sourceWriter.print("else ");
        }
        sourceWriter.println("return null;");
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
            Collection<Class<? extends Paintable>> paintablesHavingWidgetAnnotation) {
        sourceWriter
                .println("public Class<? extends Paintable> "
                        + "getImplementationByServerSideClassName(String fullyQualifiedName) {");
        sourceWriter.indent();
        sourceWriter
                .println("fullyQualifiedName = fullyQualifiedName.intern();");

        for (Class<? extends Paintable> class1 : paintablesHavingWidgetAnnotation) {
            ClientWidget annotation = class1.getAnnotation(ClientWidget.class);
            Class<? extends com.vaadin.terminal.gwt.client.Paintable> clientClass = annotation
                    .value();
            sourceWriter.print("if ( fullyQualifiedName == \"");
            sourceWriter.print(class1.getName());
            sourceWriter.print("\" ) return ");
            sourceWriter.print(clientClass.getName());
            sourceWriter.println(".class;");
            sourceWriter.print("else ");
        }
        sourceWriter
                .println("return com.vaadin.terminal.gwt.client.ui.VUnknownComponent.class;");
        sourceWriter.outdent();
        sourceWriter.println("}");

    }

}
