package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.terminal.gwt.client.ui.dd.ServerCriterion;
import com.vaadin.ui.ClientWidget;

/**
 * GWT generator to build WidgetMapImpl dynamically based on
 * {@link ClientWidget} annotations available in workspace.
 * 
 */
public class AcceptCriterionGenerator extends Generator {

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
            logger.log(TreeLogger.ERROR,
                    "Accept criterion factory creation failed", e);
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
        logger.log(Type.INFO, "Detecting available criteria ...");
        Date date = new Date();

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport("com.google.gwt.core.client.GWT");
        composer
                .setSuperclass("com.vaadin.terminal.gwt.client.ui.dd.VAcceptCriterionFactory");
        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);

        // generator constructor source code
        generateInstantiatorMethod(sourceWriter, context, logger);
        // close generated class
        sourceWriter.outdent();
        sourceWriter.println("}");
        // commit generated class
        context.commit(logger, printWriter);
        logger.log(Type.INFO, "Done. ("
                + (new Date().getTime() - date.getTime()) / 1000 + "seconds)");

    }

    private Collection<JClassType> getAvailableCriteria(GeneratorContext context) {

        Collection<JClassType> crits = new LinkedList<JClassType>();

        JClassType[] types = context.getTypeOracle().getTypes();
        for (int i = 0; i < types.length; i++) {
            JClassType jClassType = types[i];
            JClassType[] implementedInterfaces = jClassType
                    .getImplementedInterfaces();
            for (int j = 0; j < implementedInterfaces.length; j++) {
                String qualifiedSourceName = implementedInterfaces[j]
                        .getQualifiedSourceName();
                if (qualifiedSourceName
                        .equals("com.vaadin.terminal.gwt.client.ui.dd.VAcceptCriteria")) {
                    crits.add(jClassType);
                }
            }
        }
        return crits;
    }

    private void generateInstantiatorMethod(SourceWriter sourceWriter,
            GeneratorContext context, TreeLogger logger) {

        sourceWriter.println("public VAcceptCriteria get(String name) {");
        sourceWriter.indent();

        sourceWriter.println("name = name.intern();");

        Collection<JClassType> paintablesHavingWidgetAnnotation = getAvailableCriteria(context);

        for (JClassType jClassType : paintablesHavingWidgetAnnotation) {
            ServerCriterion annotation = jClassType
                    .getAnnotation(ServerCriterion.class);
            if (annotation == null) {
                // throw new RuntimeException(
                // "No server side implementation defined for "
                // + jClassType.getName());
                continue;
            } else {
                System.out.print("Printing for instantiation rule for "
                        + annotation.value());
            }
            String serversideclass = annotation.value();

            sourceWriter.print("if (\"");
            sourceWriter.print(serversideclass);
            sourceWriter.print("\" == name) return GWT.create(");
            sourceWriter.print(jClassType.getName());
            sourceWriter.println(".class );");
            sourceWriter.print("else ");

        }
        sourceWriter.println("return null;");
        sourceWriter.outdent();
        sourceWriter.println("}");
    }

}
