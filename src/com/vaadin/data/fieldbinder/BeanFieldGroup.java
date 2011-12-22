/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.fieldbinder;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidationValidator;
import com.vaadin.ui.Field;

public class BeanFieldGroup<T> extends FieldGroup {

    private Class<T> beanType;

    public BeanFieldGroup(Class<T> beanType) {
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

    /**
     * Helper method for setting the data source directly using a bean. This
     * method wraps the bean in a {@link BeanItem} and calls
     * {@link #setItemDataSource(Item)}.
     * 
     * @param bean
     *            The bean to use as data source.
     */
    public void setItemDataSource(T bean) {
        setItemDataSource(new BeanItem(bean));
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
        if (getItemDataSource() != null) {
            // The data source is set so the property must be found in the item.
            // If it is not we try to add it.
            try {
                getItemProperty(propertyId);
            } catch (BindException e) {
                // Not found, try to add a nested property;
                // BeanItem property ids are always strings so this is safe
                getItemDataSource().addNestedProperty((String) propertyId);
            }
        }

        super.bind(field, propertyId);
    }

    @Override
    protected void configureField(Field<?> field) {
        super.configureField(field);
        // Add Bean validators if there are annotations
        if (BeanValidationValidator.isImplementationAvailable()) {
            BeanValidationValidator validator = new BeanValidationValidator(
                    beanType, getPropertyId(field).toString());
            field.addValidator(validator);
            if (field.getLocale() != null) {
                validator.setLocale(field.getLocale());
            }
        }
    }

}