/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.fieldbinder;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import com.vaadin.data.fieldbinder.FieldBinder.BindException;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;

/**
 * Class for constructing form fields based on a data type.
 * <p>
 * 
 * FIXME Javadoc
 */
public class FormBuilder implements Serializable {

    private FormBuilderFieldFactory fieldFactory = new DefaultFormBuilderFieldFactory();
    private FieldBinder fieldBinder;
    private static final Logger logger = Logger.getLogger(FormBuilder.class
            .getName());

    /**
     * Constructs a FormBuilder that can be used to build forms automatically.
     * 
     * @param fieldBinder
     *            The FieldBinder to use for binding the fields to the data
     *            source
     */
    public FormBuilder(FieldBinder fieldBinder) {
        this.fieldBinder = fieldBinder;
    }

    /**
     * TODO: javadoc
     */
    protected FieldBinder getFieldBinder() {
        return fieldBinder;
    }

    /**
     * TODO: javadoc
     */
    public FormBuilderFieldFactory getFieldFactory() {
        return fieldFactory;
    }

    /**
     * TODO: javadoc
     */
    public void setFieldFactory(FormBuilderFieldFactory fieldFactory) {
        this.fieldFactory = fieldFactory;
    }

    /**
     * Builds a field and binds it to the given property id using the field
     * binder.
     * 
     * @param propertyId
     *            The property id to bind to. Must be present in the field
     *            finder.
     * @return The created and bound field
     */
    public Field<?> buildAndBind(Object propertyId) {
        String caption = DefaultFieldFactory
                .createCaptionByPropertyId(propertyId);
        return buildAndBind(caption, propertyId);
    }

    /**
     * Builds a field using the given caption and binds it to the given property
     * id using the field binder.
     * 
     * @param caption
     *            The caption for the field
     * @param propertyId
     *            The property id to bind to. Must be present in the field
     *            finder.
     * @return The created and bound field. Can be any type of {@link Field}.
     */
    public Field<?> buildAndBind(String caption, Object propertyId) {
        Class<?> type = getFieldBinder().getPropertyType(propertyId);
        return buildAndBind(caption, propertyId, type, Field.class);

    }

    /**
     * Builds a field using the given caption and binds it to the given property
     * id using the field binder. Ensures the new field is of the given type.
     * 
     * @param caption
     *            The caption for the field
     * @param propertyId
     *            The property id to bind to. Must be present in the field
     *            finder.
     * @return The created and bound field. Can be any type of {@link Field}.
     */
    public <T extends Field> T buildAndBind(String caption, Object propertyId,
            Class<T> fieldType) {
        Class<?> type = getFieldBinder().getPropertyType(propertyId);
        return buildAndBind(caption, propertyId, type, fieldType);

    }

    /**
     * Builds a field with the given type and binds it to the given property id
     * using the field binder.
     * 
     * @param caption
     *            The caption for the new field
     * @param propertyId
     *            The property id to bind to. Must be present in the field
     *            binder.
     * @param type
     *            The data model type we want to edit using the field
     * @param fieldType
     *            The type of field we want to create
     * @return The created and bound field
     */
    protected <T extends Field> T buildAndBind(String caption,
            Object propertyId, Class<?> type, Class<T> fieldType) {
        T field = build(caption, type, fieldType);
        fieldBinder.bind(field, propertyId);
        return field;
    }

    /**
     * Creates a field based on the given data type.
     * <p>
     * The data type is the type that we want to edit using the field. The field
     * type is the type of field we want to create, can be {@link Field} if any
     * Field is good.
     * </p>
     * 
     * @param caption
     *            The caption for the new field
     * @param dataType
     *            The data model type that we want to edit using the field
     * @param fieldType
     *            The type of field that we want to create
     * @return A Field capable of editing the given type
     */
    protected <T extends Field> T build(String caption, Class<?> dataType,
            Class<T> fieldType) {
        logger.finest("Building a field with caption " + caption + " of type "
                + dataType.getName());
        T field = getFieldFactory().createField(dataType, fieldType);
        if (field == null) {
            throw new BuildException("Unable to build a field of type "
                    + fieldType.getName() + " for editing "
                    + dataType.getName());
        }

        field.setCaption(caption);
        return field;
    }

    /**
     * Builds and binds fields for the given class.
     * <p>
     * This method processes all fields whose type extends {@link Field} and
     * that can be mapped to a property id. Property id mapping is done based on
     * the field name or on a {@link PropertyId} annotation on the field. All
     * fields for which a property id can be determined are built if they are
     * null and then bound to the property id. Also existing fields are bound to
     * the corresponding property id.
     * 
     * @param object
     *            The object to process
     * @throws FormBuilderException
     *             If there is a problem building or binding a field
     */
    public void buildAndBindFields(Object object) throws FormBuilderException {
        Class<?> objectClass = object.getClass();

        for (java.lang.reflect.Field f : objectClass.getDeclaredFields()) {
            PropertyId propertyIdAnnotation = f.getAnnotation(PropertyId.class);

            if (!Field.class.isAssignableFrom(f.getType())) {
                // Process next field
                continue;
            }
            Class<? extends Field> fieldType = (Class<? extends Field>) f
                    .getType();

            Object propertyId = null;
            if (propertyIdAnnotation != null) {
                // @PropertyId(propertyId) always overrides property id
                propertyId = propertyIdAnnotation.value();
            } else {
                propertyId = f.getName();
            }

            // Ensure that the property id exists
            Class<?> propertyType;

            try {
                propertyType = fieldBinder.getPropertyType(propertyId);
            } catch (BindException e) {
                // Property id was not found, skip this field
                continue;
            }

            Field<?> builtField;
            try {
                builtField = (Field<?>) ReflectTools.getJavaFieldValue(object,
                        f);
            } catch (Exception e) {
                // If we cannot determine the value, just skip the field and try
                // the next one
                continue;
            }

            if (builtField == null) {
                // Field is null -> build the field
                Caption captionAnnotation = f.getAnnotation(Caption.class);
                String caption;
                if (captionAnnotation != null) {
                    caption = captionAnnotation.value();
                } else {
                    caption = DefaultFieldFactory
                            .createCaptionByPropertyId(propertyId);
                }

                // Create the component (Field)
                builtField = build(caption, propertyType, fieldType);

                // Store it in the field
                try {
                    ReflectTools.setJavaFieldValue(object, f, builtField);
                } catch (IllegalArgumentException e) {
                    throw new BuildException(
                            "Could not assign value to field '" + f.getName()
                                    + "'", e);
                } catch (IllegalAccessException e) {
                    throw new BuildException(
                            "Could not assign value to field '" + f.getName()
                                    + "'", e);
                } catch (InvocationTargetException e) {
                    throw new BuildException(
                            "Could not assign value to field '" + f.getName()
                                    + "'", e);
                }
            }

            // Bind it to the property id
            if (builtField != null) {
                fieldBinder.bind(builtField, propertyId);
            }

        }
    }

    public static class FormBuilderException extends RuntimeException {

        public FormBuilderException() {
            super();
            // TODO Auto-generated constructor stub
        }

        public FormBuilderException(String message, Throwable cause) {
            super(message, cause);
            // TODO Auto-generated constructor stub
        }

        public FormBuilderException(String message) {
            super(message);
            // TODO Auto-generated constructor stub
        }

        public FormBuilderException(Throwable cause) {
            super(cause);
            // TODO Auto-generated constructor stub
        }

    }

    public static class BuildException extends RuntimeException {

        public BuildException(String message) {
            super(message);
        }

        public BuildException(String message, Throwable t) {
            super(message, t);
        }

    }

}
