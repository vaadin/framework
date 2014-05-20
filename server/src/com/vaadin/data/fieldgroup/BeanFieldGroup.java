/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data.fieldgroup;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.Field;

public class BeanFieldGroup<T> extends FieldGroup {

    private Class<T> beanType;

    private static Boolean beanValidationImplementationAvailable = null;
    private final Map<Field<?>, BeanValidator> defaultValidators;

    public BeanFieldGroup(Class<T> beanType) {
        this.beanType = beanType;
        this.defaultValidators = new HashMap<Field<?>, BeanValidator>();
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

    @Override
    protected Object findPropertyId(java.lang.reflect.Field memberField) {
        String fieldName = memberField.getName();
        Item dataSource = getItemDataSource();
        if (dataSource != null && dataSource.getItemProperty(fieldName) != null) {
            return fieldName;
        } else {
            String minifiedFieldName = minifyFieldName(fieldName);
            try {
                return getFieldName(beanType, minifiedFieldName);
            } catch (SecurityException e) {
            } catch (NoSuchFieldException e) {
            }
        }
        return null;
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
            } catch (NoSuchFieldException e) {
                // Try super classes until we reach Object
                Class<?> superClass = cls.getSuperclass();
                if (superClass != null && superClass != Object.class) {
                    return getField(superClass, propertyId);
                } else {
                    throw e;
                }
            }
        }
    }

    private static String getFieldName(Class<?> cls, String propertyId)
            throws SecurityException, NoSuchFieldException {
        for (java.lang.reflect.Field field1 : cls.getDeclaredFields()) {
            if (propertyId.equals(minifyFieldName(field1.getName()))) {
                return field1.getName();
            }
        }
        // Try super classes until we reach Object
        Class<?> superClass = cls.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            return getFieldName(superClass, propertyId);
        } else {
            throw new NoSuchFieldException();
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

    private void ensureNestedPropertyAdded(Object propertyId) {
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
    }

    @Override
    public void bind(Field field, Object propertyId) {
        ensureNestedPropertyAdded(propertyId);
        super.bind(field, propertyId);
    }

    @Override
    public Field<?> buildAndBind(String caption, Object propertyId)
            throws BindException {
        ensureNestedPropertyAdded(propertyId);
        return super.buildAndBind(caption, propertyId);
    }

    @Override
    public void unbind(Field<?> field) throws BindException {
        super.unbind(field);

        BeanValidator removed = defaultValidators.remove(field);
        if (removed != null) {
            field.removeValidator(removed);
        }
    }

    @Override
    protected void configureField(Field<?> field) {
        super.configureField(field);
        // Add Bean validators if there are annotations
        if (isBeanValidationImplementationAvailable()
                && !defaultValidators.containsKey(field)) {
            BeanValidator validator = new BeanValidator(beanType,
                    getPropertyId(field).toString());
            field.addValidator(validator);
            if (field.getLocale() != null) {
                validator.setLocale(field.getLocale());
            }
            defaultValidators.put(field, validator);
        }
    }

    /**
     * Checks whether a bean validation implementation (e.g. Hibernate Validator
     * or Apache Bean Validation) is available.
     * 
     * TODO move this method to some more generic location
     * 
     * @return true if a JSR-303 bean validation implementation is available
     */
    protected static boolean isBeanValidationImplementationAvailable() {
        if (beanValidationImplementationAvailable != null) {
            return beanValidationImplementationAvailable;
        }
        try {
            Class<?> validationClass = Class
                    .forName("javax.validation.Validation");
            Method buildFactoryMethod = validationClass
                    .getMethod("buildDefaultValidatorFactory");
            Object factory = buildFactoryMethod.invoke(null);
            beanValidationImplementationAvailable = (factory != null);
        } catch (Exception e) {
            // no bean validation implementation available
            beanValidationImplementationAvailable = false;
        }
        return beanValidationImplementationAvailable;
    }

    /**
     * Convenience method to bind Fields from a given "field container" to a
     * given bean with buffering disabled.
     * <p>
     * The returned {@link BeanFieldGroup} can be used for further
     * configuration.
     * 
     * @see #bindFieldsBuffered(Object, Object)
     * @see #bindMemberFields(Object)
     * @since 7.2
     * @param bean
     *            the bean to be bound
     * @param objectWithMemberFields
     *            the class that contains {@link Field}s for bean properties
     * @return the bean field group used to make binding
     */
    public static <T> BeanFieldGroup<T> bindFieldsUnbuffered(T bean,
            Object objectWithMemberFields) {
        return createAndBindFields(bean, objectWithMemberFields, false);
    }

    /**
     * Convenience method to bind Fields from a given "field container" to a
     * given bean with buffering enabled.
     * <p>
     * The returned {@link BeanFieldGroup} can be used for further
     * configuration.
     * 
     * @see #bindFieldsUnbuffered(Object, Object)
     * @see #bindMemberFields(Object)
     * @since 7.2
     * @param bean
     *            the bean to be bound
     * @param objectWithMemberFields
     *            the class that contains {@link Field}s for bean properties
     * @return the bean field group used to make binding
     */
    public static <T> BeanFieldGroup<T> bindFieldsBuffered(T bean,
            Object objectWithMemberFields) {
        return createAndBindFields(bean, objectWithMemberFields, true);
    }

    private static <T> BeanFieldGroup<T> createAndBindFields(T bean,
            Object objectWithMemberFields, boolean buffered) {
        @SuppressWarnings("unchecked")
        BeanFieldGroup<T> beanFieldGroup = new BeanFieldGroup<T>(
                (Class<T>) bean.getClass());
        beanFieldGroup.setItemDataSource(bean);
        beanFieldGroup.setBuffered(buffered);
        beanFieldGroup.bindMemberFields(objectWithMemberFields);
        return beanFieldGroup;
    }

}
