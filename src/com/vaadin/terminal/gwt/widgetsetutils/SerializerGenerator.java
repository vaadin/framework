/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.communication.JSONSerializer;
import com.vaadin.terminal.gwt.client.communication.JsonDecoder;
import com.vaadin.terminal.gwt.client.communication.JsonEncoder;
import com.vaadin.terminal.gwt.client.communication.SerializerMap;

/**
 * GWT generator for creating serializer classes for custom classes sent from
 * server to client.
 * 
 * Only fields with a correspondingly named setter are deserialized.
 * 
 * @since 7.0
 */
public class SerializerGenerator extends Generator {

    private static final String SUBTYPE_SEPARATOR = "___";
    private static String beanSerializerPackageName = SerializerMap.class
            .getPackage().getName();

    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
            String beanTypeName) throws UnableToCompleteException {
        JClassType beanType = context.getTypeOracle().findType(beanTypeName);
        String beanSerializerClassName = getSerializerSimpleClassName(beanType);
        try {
            // Generate class source code
            generateClass(logger, context, beanType, beanSerializerPackageName,
                    beanSerializerClassName);
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR, "SerializerGenerator failed for "
                    + beanType.getQualifiedSourceName(), e);
        }
        // return the fully qualifed name of the class generated
        logger.log(TreeLogger.INFO, "Generated Serializer class "
                + getFullyQualifiedSerializerClassName(beanType));
        return getFullyQualifiedSerializerClassName(beanType);
    }

    /**
     * Generate source code for a VaadinSerializer implementation.
     * 
     * @param logger
     *            Logger object
     * @param context
     *            Generator context
     * @param beanType
     * @param beanTypeName
     *            bean type for which the serializer is to be generated
     * @param beanSerializerTypeName
     *            name of the serializer class to generate
     */
    private void generateClass(TreeLogger logger, GeneratorContext context,
            JClassType beanType, String serializerPackageName,
            String serializerClassName) {
        // get print writer that receives the source code
        PrintWriter printWriter = null;
        printWriter = context.tryCreate(logger, serializerPackageName,
                serializerClassName);

        // print writer if null, source code has ALREADY been generated
        if (printWriter == null) {
            return;
        }
        Date date = new Date();
        TypeOracle typeOracle = context.getTypeOracle();
        String beanQualifiedSourceName = beanType.getQualifiedSourceName();
        logger.log(Type.DEBUG, "Processing serializable type "
                + beanQualifiedSourceName + "...");

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(serializerPackageName,
                serializerClassName);
        composer.addImport(GWT.class.getName());
        composer.addImport(JSONArray.class.getName());
        // composer.addImport(JSONObject.class.getName());
        // composer.addImport(VPaintableMap.class.getName());
        composer.addImport(JsonDecoder.class.getName());
        // composer.addImport(VaadinSerializer.class.getName());

        composer.addImplementedInterface(JSONSerializer.class.getName());

        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);
        sourceWriter.indent();

        // Serializer

        // public JSONValue serialize(Object value, ConnectorMap idMapper,
        // ApplicationConnection connection) {
        sourceWriter.println("public " + JSONObject.class.getName()
                + " serialize(" + Object.class.getName() + " value, "
                + ConnectorMap.class.getName() + " idMapper, "
                + ApplicationConnection.class.getName() + " connection) {");
        sourceWriter.indent();
        // MouseEventDetails castedValue = (MouseEventDetails) value;
        sourceWriter.println(beanQualifiedSourceName + " castedValue = ("
                + beanQualifiedSourceName + ") value;");
        // JSONObject json = new JSONObject();
        sourceWriter.println(JSONObject.class.getName() + " json = new "
                + JSONObject.class.getName() + "();");

        for (JMethod setterMethod : getSetters(beanType)) {
            String setterName = setterMethod.getName();
            String capitalizedFieldName = setterName.substring(3);
            String fieldName = decapitalize(capitalizedFieldName);
            String getterName = findGetter(beanType, setterMethod);

            if (getterName == null) {
                logger.log(TreeLogger.WARN, "No getter found for " + fieldName
                        + ". Serialization will likely fail");
            }
            // json.put("button",
            // JsonEncoder.encode(castedValue.getButton(), idMapper,
            // connection));
            sourceWriter.println("json.put(\"" + fieldName + "\", "
                    + JsonEncoder.class.getName() + ".encode(castedValue."
                    + getterName + "(), idMapper, connection));");
        }
        // return json;
        sourceWriter.println("return json;");
        // }
        sourceWriter.println("}");

        // Deserializer
        sourceWriter.println("public " + beanQualifiedSourceName
                + " deserialize(" + JSONObject.class.getName() + " jsonValue, "
                + ConnectorMap.class.getName() + " idMapper, "
                + ApplicationConnection.class.getName() + " connection) {");
        sourceWriter.indent();

        // VButtonState state = GWT.create(VButtonState.class);
        sourceWriter.println(beanQualifiedSourceName + " state = GWT.create("
                + beanQualifiedSourceName + ".class);");
        for (JMethod method : getSetters(beanType)) {
            String setterName = method.getName();
            String capitalizedFieldName = setterName.substring(3);
            String fieldName = decapitalize(capitalizedFieldName);
            JType setterParameterType = method.getParameterTypes()[0];

            logger.log(Type.INFO, "* Processing field " + fieldName + " in "
                    + beanQualifiedSourceName + " (" + beanType.getName() + ")");

            String jsonFieldName = "json" + capitalizedFieldName;
            // JSONArray jsonHeight = (JSONArray) jsonValue.get("height");
            sourceWriter.println("JSONArray " + jsonFieldName
                    + " = (JSONArray) jsonValue.get(\"" + fieldName + "\");");

            // state.setHeight((String)
            // JsonDecoder.decodeValue(jsonFieldValue,idMapper, connection));

            String fieldType;
            JPrimitiveType primitiveType = setterParameterType.isPrimitive();
            if (primitiveType != null) {
                // This is a primitive type -> must used the boxed type
                fieldType = primitiveType.getQualifiedBoxedSourceName();
            } else {
                fieldType = setterParameterType.getQualifiedSourceName();
            }

            sourceWriter.println("state." + setterName + "((" + fieldType
                    + ") " + JsonDecoder.class.getName() + ".decodeValue("
                    + jsonFieldName + ", idMapper, connection));");
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

    private String findGetter(JClassType beanType, JMethod setterMethod) {
        JType setterParameterType = setterMethod.getParameterTypes()[0];
        String capitalizedFieldName = setterMethod.getName().substring(3);
        if (setterParameterType.getQualifiedSourceName().equals(
                boolean.class.getName())) {
            return "is" + capitalizedFieldName;
        } else {
            return "get" + capitalizedFieldName;
        }
    }

    /**
     * Returns a list of all setters found in the beanType or its parent class
     * 
     * @param beanType
     *            The type to check
     * @return A list of setter methods from the class and its parents
     */
    protected static List<JMethod> getSetters(JClassType beanType) {

        List<JMethod> setterMethods = new ArrayList<JMethod>();

        while (!beanType.getQualifiedSourceName()
                .equals(Object.class.getName())) {
            for (JMethod method : beanType.getMethods()) {
                // Process all setters that have corresponding fields
                if (!method.isPublic() || method.isStatic()
                        || !method.getName().startsWith("set")
                        || method.getParameterTypes().length != 1) {
                    // Not setter, skip to next method
                    continue;
                }
                setterMethods.add(method);
            }
            beanType = beanType.getSuperclass();
        }

        return setterMethods;
    }

    private String decapitalize(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    private static String getSerializerSimpleClassName(JClassType beanType) {
        return getSimpleClassName(beanType) + "_Serializer";
    }

    private static String getSimpleClassName(JClassType type) {
        if (type.isMemberType()) {
            // Assumed to be static sub class
            String baseName = getSimpleClassName(type.getEnclosingType());
            String name = baseName + SUBTYPE_SEPARATOR
                    + type.getSimpleSourceName();
            return name;
        }
        return type.getSimpleSourceName();
    }

    public static String getFullyQualifiedSerializerClassName(JClassType type) {
        return beanSerializerPackageName + "."
                + getSerializerSimpleClassName(type);
    }
}
