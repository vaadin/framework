/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ClientCriterion;
import com.vaadin.terminal.gwt.client.ui.dd.VAcceptCriterion;
import com.vaadin.terminal.gwt.client.ui.dd.VAcceptCriterionFactory;

/**
 * GWT generator to build {@link VAcceptCriterionFactory} implementation
 * dynamically based on {@link ClientCriterion} annotations available in
 * classpath.
 * 
 */
public class AcceptCriteriaFactoryGenerator extends Generator {

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

    private void generateInstantiatorMethod(SourceWriter sourceWriter,
            GeneratorContext context, TreeLogger logger) {

        sourceWriter.println("public VAcceptCriterion get(String name) {");
        sourceWriter.indent();

        sourceWriter.println("name = name.intern();");

        Collection<Class<? extends AcceptCriterion>> clientSideVerifiableCriterion = ClassPathExplorer
                .getCriterion();

        for (Class<? extends AcceptCriterion> class1 : clientSideVerifiableCriterion) {
            logger.log(Type.INFO, "creating mapping for "
                    + class1.getCanonicalName());
            String canonicalName = class1.getCanonicalName();
            Class<? extends VAcceptCriterion> clientClass = class1
                    .getAnnotation(ClientCriterion.class).value();
            sourceWriter.print("if (\"");
            sourceWriter.print(canonicalName);
            sourceWriter.print("\" == name) return GWT.create(");
            sourceWriter.print(clientClass.getCanonicalName());
            sourceWriter.println(".class );");
            sourceWriter.print("else ");
        }

        sourceWriter.println("return null;");
        sourceWriter.outdent();
        sourceWriter.println("}");
    }

}
