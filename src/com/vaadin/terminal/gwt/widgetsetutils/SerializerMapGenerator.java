/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.PrintWriter;
import java.util.Date;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.terminal.gwt.client.communication.SerializerMap;
import com.vaadin.terminal.gwt.client.communication.SharedState;
import com.vaadin.terminal.gwt.client.communication.VaadinSerializer;

/**
 * GWT generator that creates a {@link SerializerMap} implementation (mapper
 * from type string to serializer instance) and serializer classes for all
 * subclasses of {@link SharedState}.
 * 
 * @since 7.0
 */
public class SerializerMapGenerator extends Generator {

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
            // Generate class source code for SerializerMapImpl
            generateClass(logger, context);

            // Generate serializer classes for each subclass of SharedState
            JClassType serializerType = typeOracle.findType(SharedState.class
                    .getName());
            JClassType[] serializerSubtypes = serializerType.getSubtypes();
            SerializerGenerator sg = new SerializerGenerator();
            for (JClassType type : serializerSubtypes) {
                sg.generate(logger, context, type.getQualifiedSourceName());
            }
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR,
                    "SerializerMapGenerator creation failed", e);
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
        logger.log(Type.INFO, "Detecting serializable data types...");
        Date date = new Date();
        TypeOracle typeOracle = context.getTypeOracle();
        JClassType serializerType = typeOracle.findType(SharedState.class
                .getName());
        JClassType[] serializerSubtypes = serializerType.getSubtypes();

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport("com.google.gwt.core.client.GWT");
        composer.addImplementedInterface(SerializerMap.class.getName());
        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);
        sourceWriter.indent();

        sourceWriter.println("public " + VaadinSerializer.class.getName()
                + " getSerializer(String type) {");
        sourceWriter.indent();

        // TODO cache serializer instances in a map
        for (JClassType type : serializerSubtypes) {
            sourceWriter.println("if (type.equals(\""
                    + type.getQualifiedSourceName() + "\")) {");
            sourceWriter.indent();
            sourceWriter.println("return GWT.create("
                    + type.getQualifiedSourceName() + "_Serializer.class);");
            sourceWriter.outdent();
            sourceWriter.println("}");
            logger.log(Type.INFO, "Configured serializer for " + type.getName());
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
}
