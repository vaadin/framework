package com.vaadin.data.fieldbinder;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.Field;

public class BeanFieldBinder<T> extends FieldBinder {

    private Class<T> beanType;

    public BeanFieldBinder(Class<T> beanType) {
        this.beanType = beanType;
    }

    @Override
    protected Class<?> getPropertyType(Object propertyId) {
        if (getItemDataSource() != null) {
            return super.getPropertyType(propertyId);
        } else {
            // Data source not set so we need to figure out the type manually
            /*
             * toString should never really be needed as propertyId should be of
             * form "fieldName" or "fieldName.subField[.subField2]" but the
             * method declaration comes from parent.
             */
            java.lang.reflect.Field f;
            try {
                f = getField(beanType, propertyId.toString());
                return f.getType();
            } catch (SecurityException e) {
                throw new BindException("Cannot determine type of propertyId '"
                        + propertyId + "'.", e);
            } catch (NoSuchFieldException e) {
                throw new BindException("Cannot determine type of propertyId '"
                        + propertyId + "'. The propertyId was not found in "
                        + beanType.getName(), e);
            }
        }
    }

    private static java.lang.reflect.Field getField(Class<?> cls,
            String propertyId) throws SecurityException, NoSuchFieldException {
        if (propertyId.contains(".")) {
            String[] parts = propertyId.split("\\.", 2);
            // Get the type of the field in the "cls" class
            java.lang.reflect.Field field1 = getField(cls, parts[0]);
            // Find the rest from the sub type
            return getField(field1.getType(), parts[1]);
        } else {
            try {
                // Try to find the field directly in the given class
                java.lang.reflect.Field field1 = cls
                        .getDeclaredField(propertyId);
                return field1;
            } catch (NoSuchFieldError e) {
                // Try super classes until we reach Object
                Class<?> superClass = cls.getSuperclass();
                if (superClass != Object.class) {
                    return getField(superClass, propertyId);
                } else {
                    throw e;
                }
            }
        }
    }

    @Override
    public void setItemDataSource(Item item) {
        if (!(item instanceof BeanItem)) {
            throw new RuntimeException(getClass().getSimpleName()
                    + " only supports BeanItems as item data source");
        }
        super.setItemDataSource(item);
    }

    @Override
    public BeanItem<T> getItemDataSource() {
        return (BeanItem<T>) super.getItemDataSource();
    }

    @Override
    public void bind(Field field, Object propertyId) {
        try {
            super.bind(field, propertyId);
        } catch (BindException e) {
            // System.out.println("Trying to add nested property " +
            // propertyId);
            if (getItemDataSource() != null
                    && field.getPropertyDataSource() == null) {
                // Workaround for nested properties in BeanItem.

                // FIXME: BeanItem.setAddNestedPropertiesAutomatically would be
                // a nice, real fix..
                Property p = new NestedMethodProperty(getItemDataSource()
                        .getBean(), (String) propertyId);
                getItemDataSource().addItemProperty(propertyId, p);
                super.bind(field, propertyId);
            }
        }

    }

    // private static void addFieldsFromType(List<Object> fields, Class<?> type,
    // String prefix) {
    // for (java.lang.reflect.Field f : getAllFields(type)) {
    // if (isSimpleType(f)) {
    // fields.add(prefix + f.getName());
    // } else {
    // String subPrefix = f.getName() + ".";
    // if (!prefix.equals("")) {
    // subPrefix = prefix + subPrefix;
    // }
    //
    // addFieldsFromType(fields, f.getType(), subPrefix);
    // }
    //
    // }
    //
    // }

    // /**
    // * Return a list of all fields in the given class. Includes private fields
    // * and also fields from super classes.
    // *
    // * @param type
    // * @return
    // */
    // private static List<java.lang.reflect.Field> getAllFields(Class<?> type)
    // {
    // System.out.println("getAllFields for " + type.getName());
    // List<java.lang.reflect.Field> fields = new
    // ArrayList<java.lang.reflect.Field>();
    //
    // Class<?> superClass = type.getSuperclass();
    // if (superClass != Object.class) {
    // fields.addAll(getAllFields(superClass));
    // }
    //
    // for (java.lang.reflect.Field f : type.getDeclaredFields()) {
    // fields.add(f);
    // }
    //
    // return fields;
    // }

    // private static boolean isSimpleType(java.lang.reflect.Field f) {
    // // FIXME: What other classes? Boolean, Integer etc. How do we know what
    // // is a mapped class that should be expanded? Or should we make the user
    // // provide them?
    // Class<?> type = f.getType();
    // if (type.isPrimitive()) {
    // return true;
    // }
    // if (Enum.class.isAssignableFrom(type)) {
    // return true;
    // }
    // if (Number.class.isAssignableFrom(type)) {
    // return true;
    // }
    // if (CharSequence.class.isAssignableFrom(type)) {
    // return true;
    // }
    //
    // return false;
    // }

}