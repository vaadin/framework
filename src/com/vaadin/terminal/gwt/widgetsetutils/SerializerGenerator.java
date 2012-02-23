/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.PrintWriter;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.communication.JsonDecoder;
import com.vaadin.terminal.gwt.client.communication.VaadinSerializer;

/**
 * GWT generator for creating serializer classes for custom classes sent from
 * server to client.
 * 
 * Only fields with a correspondingly named setter are deserialized.
 * 
 * @since 7.0
 */
public class SerializerGenerator extends Generator {

    private String packageName;
    private String beanSerializerClassName;

    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
            String beanTypeName) throws UnableToCompleteException {
        String beanSerializerTypeName = beanTypeName + "_Serializer";
        try {
            TypeOracle typeOracle = context.getTypeOracle();

            // get classType and save instance variables
            JClassType classType = typeOracle.getType(beanTypeName);
            packageName = classType.getPackage().getName();
            beanSerializerClassName = classType.getSimpleSourceName()
                    + "_Serializer";
            // Generate class source code
            generateClass(logger, context, beanTypeName, beanSerializerTypeName);
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR, "SerializerGenerator failed for "
                    + beanTypeName, e);
        }
        // return the fully qualifed name of the class generated
        return packageName + "." + beanSerializerClassName;
    }

    /**
     * Generate source code for a VaadinSerializer implementation.
     * 
     * @param logger
     *            Logger object
     * @param context
     *            Generator context
     * @param beanTypeName
     *            bean type for which the serializer is to be generated
     * @param beanSerializerTypeName
     *            name of the serializer class to generate
     */
    private void generateClass(TreeLogger logger, GeneratorContext context,
            String beanTypeName, String beanSerializerTypeName) {
        // get print writer that receives the source code
        PrintWriter printWriter = null;
        printWriter = context.tryCreate(logger, packageName,
                beanSerializerClassName);

        // print writer if null, source code has ALREADY been generated
        if (printWriter == null) {
            return;
        }
        Date date = new Date();
        TypeOracle typeOracle = context.getTypeOracle();
        logger.log(Type.DEBUG, "Processing serializable type " + beanTypeName
                + "...");

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName,
                beanSerializerClassName);
        composer.addImport(GWT.class.getName());
        composer.addImport(JSONArray.class.getName());
        // composer.addImport(JSONObject.class.getName());
        // composer.addImport(VPaintableMap.class.getName());
        composer.addImport(JsonDecoder.class.getName());
        // composer.addImport(VaadinSerializer.class.getName());

        composer.addImplementedInterface(VaadinSerializer.class.getName());

        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);
        sourceWriter.indent();

        sourceWriter.println("public " + beanTypeName + " deserialize("
                + JSONObject.class.getName() + " jsonValue, "
                + VPaintableMap.class.getName() + " idMapper) {");
        sourceWriter.indent();

        // VButtonState state = GWT.create(VButtonState.class);
        sourceWriter.println(beanTypeName + " state = GWT.create("
                + beanTypeName + ".class);");
        JClassType beanType = typeOracle.findType(beanTypeName);
        JClassType objectType = typeOracle.findType("java.lang.Object");
        while (!objectType.equals(beanType)) {
            for (JMethod method : beanType.getMethods()) {
                // Process all setters that have corresponding fields
                if (!method.isPublic() || method.isStatic()
                        || !method.getName().startsWith("set")
                        || method.getParameterTypes().length != 1) {
                    // Not setter, skip to next method
                    continue;

                }
                String setterName = method.getName();
                String capitalizedFieldName = setterName.substring(3);
                String fieldName = decapitalize(capitalizedFieldName);
                JType setterParameterType = method.getParameterTypes()[0];

                logger.log(Type.DEBUG, "* Processing field " + fieldName
                        + " in " + beanTypeName + " (" + beanType.getName()
                        + ")");

                String jsonFieldName = "json" + capitalizedFieldName;
                // JSONArray jsonHeight = (JSONArray) jsonValue.get("height");
                sourceWriter.println("JSONArray " + jsonFieldName
                        + " = (JSONArray) jsonValue.get(\"" + fieldName
                        + "\");");

                // state.setHeight((String)
                // JsonDecoder.convertValue(jsonFieldValue,idMapper));

                String fieldType;
                JPrimitiveType primitiveType = setterParameterType
                        .isPrimitive();
                if (primitiveType != null) {
                    // This is a primitive type -> must used the boxed type
                    fieldType = primitiveType.getQualifiedBoxedSourceName();
                } else {
                    fieldType = setterParameterType.getQualifiedSourceName();
                }

                sourceWriter.println("state." + setterName + "((" + fieldType
                        + ") JsonDecoder.convertValue(" + jsonFieldName
                        + ", idMapper));");
            }
            beanType = beanType.getSuperclass();
        }

        // return state;
        sourceWriter.println("return state;");
        sourceWriter.println("}");
        sourceWriter.outdent();

        // End of class
        sourceWriter.println("}");
        sourceWriter.outdent();

        // commit generated class
        context.commit(logger, printWriter);
        logger.log(Type.INFO,
                "Done. (" + (new Date().getTime() - date.getTime()) / 1000
                        + "seconds)");

    }

    private String decapitalize(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
}
