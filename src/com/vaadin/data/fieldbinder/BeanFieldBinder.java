/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.fieldbinder;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.validator.BeanValidationValidator;
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

    @Override
    protected void configureField(Field<?> field) {
        super.configureField(field);
        // Add Bean validators if there are annotations
        BeanValidationValidator validator = BeanValidationValidator
                .addValidator(field, getPropertyIdForField(field), beanType);
        if (field.getLocale() != null) {
            validator.setLocale(field.getLocale());
        }
    }

}