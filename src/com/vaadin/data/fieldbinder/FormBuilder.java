package com.vaadin.data.fieldbinder;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.vaadin.data.fieldbinder.FieldBinder.BindException;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;

/**
 * FIXME Javadoc
 * 
 */
public class FormBuilder {

    private FieldBinderFieldFactory fieldFactory = new com.vaadin.data.fieldbinder.DefaultFieldBinderFieldFactory();
    private FieldBinder fieldBinder;

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
    public FieldBinderFieldFactory getFieldFactory() {
        return fieldFactory;
    }

    /**
     * TODO: javadoc
     */
    public void setFieldFactory(FieldBinderFieldFactory fieldFactory) {
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
     * @return The created and bound field
     */
    public Field<?> buildAndBind(String caption, Object propertyId) {
        Class<?> type = getFieldBinder().getPropertyType(propertyId);
        return buildAndBind(caption, propertyId, type);

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
     * @return The created and bound field
     */
    protected Field<?> buildAndBind(String caption, Object propertyId,
            Class<?> type) {
        Field<?> field = build(caption, type);
        fieldBinder.bind(field, propertyId);
        return field;
    }

    /**
     * Creates a field based on the given type. The type should be the data
     * model type and not a Vaadin Field type.
     * 
     * @param caption
     *            The caption for the new field
     * @param type
     *            The data model type that we want to edit using the field
     * @return A Field capable of editing the given type
     */
    protected Field<?> build(String caption, Class<?> type) {
        System.err.println("Building a field with caption " + caption
                + " of type " + type.getName());
        Field<?> field = getFieldFactory().createField(type);
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
        buildAndBindFields(object, false);
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
     * @param onlyBind
     *            true if only binding should be done, false if also building
     *            should be done when necessary
     * @throws FormBuilderException
     *             If there is a problem building or binding a field
     */
    public void buildAndBindFields(Object object, boolean onlyBind)
            throws FormBuilderException {
        Class<?> objectClass = object.getClass();

        for (java.lang.reflect.Field f : objectClass.getDeclaredFields()) {
            PropertyId propertyIdAnnotation = f.getAnnotation(PropertyId.class);

            if (!Field.class.isAssignableFrom(f.getType())) {
                // Process next field
                continue;
            }

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
                builtField = (Field<?>) getJavaFieldValue(object, f);
            } catch (Exception e) {
                // If we cannot determine the value, just skip the field and try
                // the next one
                continue;
            }

            if (builtField == null && !onlyBind) {
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
                builtField = build(caption, propertyType);
                // Store it in the field
                setJavaFieldValue(object, f, builtField);
            }

            // Bind it to the property id
            if (builtField != null) {
                fieldBinder.bind(builtField, propertyId);
            }

        }
    }

    /**
     * Returns the value of the java field.
     * <p>
     * Uses getter if present, otherwise tries to access even private fields.
     * 
     * @param object
     *            The object containing the field
     * @param field
     *            The field we want to get the value for
     * @return The value of the field in the object
     * @throws FormBuilderException
     *             If the field value cannot be determined
     */
    protected Object getJavaFieldValue(Object object,
            java.lang.reflect.Field field) throws FormBuilderException {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(field.getName(), object.getClass());
            Method getter = pd.getReadMethod();
            if (getter != null) {
                return getter.invoke(object, (Object[]) null);
            }
        } catch (Exception e) {
            // Ignore all problems with getter and try to get the value directly
            // from the field
        }

        try {
            if (!field.isAccessible()) {
                // Try to gain access even if field is private
                field.setAccessible(true);
            }
            return field.get(object);
        } catch (IllegalArgumentException e) {
            throw new FormBuilderException("Could not get value for field '"
                    + field.getName() + "'", e.getCause());
        } catch (IllegalAccessException e) {
            throw new FormBuilderException(
                    "Access denied while assigning built component to field '"
                            + field.getName() + "' in "
                            + object.getClass().getName(), e);
        }
    }

    /**
     * Sets the value of a java field.
     * <p>
     * Uses setter if present, otherwise tries to access even private fields
     * directly.
     * 
     * @param object
     *            The object containing the field
     * @param field
     *            The field we want to set the value for
     * @param value
     *            The value to set
     * @throws FormBuilderException
     *             If the value could not be assigned to the field
     */
    protected void setJavaFieldValue(Object object,
            java.lang.reflect.Field field, Object value)
            throws FormBuilderException {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(field.getName(), object.getClass());
            Method setter = pd.getWriteMethod();
            if (setter != null) {
                try {
                    setter.invoke(object, value);
                } catch (IllegalArgumentException e) {
                    throw new FormBuilderException(
                            "Could not assign built component to field '"
                                    + field.getName() + "'", e);
                } catch (IllegalAccessException e) {
                    throw new FormBuilderException(
                            "Access denied while assigning built component to field using "
                                    + setter.getName() + " in "
                                    + object.getClass().getName(), e);
                } catch (InvocationTargetException e) {
                    throw new FormBuilderException(
                            "Could not assign built component to field '"
                                    + field.getName() + "'", e.getCause());
                }
            }
        } catch (IntrospectionException e1) {
            // Ignore this and try to set directly using the field
        }

        try {
            if (!field.isAccessible()) {
                // Try to gain access even if field is private
                field.setAccessible(true);
            }
            field.set(object, value);
        } catch (IllegalArgumentException e) {
            throw new FormBuilderException(
                    "Could not assign built component to field '"
                            + field.getName() + "'", e.getCause());
        } catch (IllegalAccessException e) {
            throw new FormBuilderException(
                    "Access denied while assigning built component to field '"
                            + field.getName() + "' in "
                            + object.getClass().getName(), e);
        }
    }

    // /**
    // * Constructs fields for all properties in the data source and adds them
    // to
    // * the given component container. The order of the fields is determined by
    // * the order in which the item returns its property ids.
    // *
    // * This is pretty much what the old Form class used to do.
    // *
    // * @param cc
    // * The ComponentContainer where fields should be added.
    // */
    // public void buildAndBindEverything(ComponentContainer cc) {
    // Item ds = getFieldBinder().getItemDataSource();
    // for (Object propertyId : ds.getItemPropertyIds()) {
    // Field<?> f = buildAndBind(propertyId);
    // cc.addComponent(f);
    // }
    //
    // }

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

}
