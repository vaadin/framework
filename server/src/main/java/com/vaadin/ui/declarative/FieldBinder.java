/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.ui.declarative;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import com.vaadin.ui.Component;

/**
 * Binder utility that binds member fields of a design class instance to given
 * component instances. Only fields of type {@link Component} are bound
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class FieldBinder implements Serializable {

    // the instance containing the bound fields
    private Object bindTarget;
    // mapping between field names and Fields
    private Map<String, Field> fieldMap = new HashMap<>();

    /**
     * Creates a new instance of LayoutFieldBinder.
     *
     * @param design
     *            the design class instance containing the fields to bind
     * @throws IntrospectionException
     *             if the given design class can not be introspected
     */
    public FieldBinder(Object design) throws IntrospectionException {
        this(design, design.getClass());
    }

    /**
     * Creates a new instance of LayoutFieldBinder.
     *
     * @param design
     *            the instance containing the fields
     * @param classWithFields
     *            the class which defines the fields to bind
     * @throws IntrospectionException
     *             if the given design class can not be introspected
     */
    public FieldBinder(Object design, Class<?> classWithFields)
            throws IntrospectionException {
        if (design == null) {
            throw new IllegalArgumentException("The design must not be null");
        }
        bindTarget = design;
        resolveFields(classWithFields);
    }

    /**
     * Returns a collection of field names that are not bound.
     *
     * @return a collection of fields assignable to Component that are not bound
     */
    public Collection<String> getUnboundFields() throws FieldBindingException {
        List<String> unboundFields = new ArrayList<>();
        for (Field f : fieldMap.values()) {
            try {
                Object value = getFieldValue(bindTarget, f);
                if (value == null) {
                    unboundFields.add(f.getName());
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new FieldBindingException("Could not get field value", e);
            }
        }
        if (!unboundFields.isEmpty()) {
            getLogger().severe(
                    "Found unbound fields in component root :" + unboundFields);
        }
        return unboundFields;
    }

    /**
     * Resolves the fields of the design class instance.
     */
    private void resolveFields(Class<?> classWithFields) {
        for (Field memberField : getFields(classWithFields)) {
            if (Component.class.isAssignableFrom(memberField.getType())) {
                fieldMap.put(memberField.getName().toLowerCase(Locale.ROOT),
                        memberField);
            }
        }
    }

    /**
     * Tries to bind the given {@link Component} instance to a member field of
     * the bind target. The name of the bound field is constructed based on the
     * id or caption of the instance, depending on which one is defined. If a
     * field is already bound (not null), {@link FieldBindingException} is
     * thrown.
     *
     * @param instance
     *            the instance to be bound to a field
     * @return true on success, otherwise false
     * @throws FieldBindingException
     *             if error occurs when trying to bind the instance to a field
     */
    public boolean bindField(Component instance) {
        return bindField(instance, null);
    }

    /**
     * Tries to bind the given {@link Component} instance to a member field of
     * the bind target. The fields are matched based on localId, id and caption.
     *
     * @param instance
     *            the instance to be bound to a field
     * @param localId
     *            the localId used for mapping the field to an instance field
     * @return true on success
     * @throws FieldBindingException
     *             if error occurs when trying to bind the instance to a field
     */
    public boolean bindField(Component instance, String localId) {
        // check that the field exists, is correct type and is null
        boolean success = bindFieldByIdentifier(localId, instance);
        if (!success) {
            success = bindFieldByIdentifier(instance.getId(), instance);
        }
        if (!success) {
            success = bindFieldByIdentifier(instance.getCaption(), instance);
        }
        if (!success) {
            String idInfo = "localId: " + localId + " id: " + instance.getId()
                    + " caption: " + instance.getCaption();
            getLogger().finest("Could not bind component to a field "
                    + instance.getClass().getName() + " " + idInfo);
        }
        return success;
    }

    /**
     * Tries to bind the given {@link Component} instance to a member field of
     * the bind target. The field is matched based on the given identifier. If a
     * field is already bound (not null), {@link FieldBindingException} is
     * thrown.
     *
     * @param identifier
     *            the identifier for the field.
     * @param instance
     *            the instance to be bound to a field
     * @return true on success
     * @throws FieldBindingException
     *             if error occurs when trying to bind the instance to a field
     */
    private boolean bindFieldByIdentifier(String identifier,
            Component instance) {
        try {
            // create and validate field name
            String fieldName = asFieldName(identifier);
            if (fieldName.isEmpty()) {
                return false;
            }
            // validate that the field can be found
            Field field = fieldMap.get(fieldName.toLowerCase(Locale.ROOT));
            if (field == null) {
                getLogger()
                        .fine("No field was found by identifier " + identifier);
                return false;
            }
            // validate that the field is not set
            Object fieldValue = getFieldValue(bindTarget, field);
            if (fieldValue != null) {
                getLogger().fine("The field \"" + fieldName
                        + "\" was already mapped. Ignoring.");
            } else {
                // set the field value
                field.set(bindTarget, instance);
            }
            return true;
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new FieldBindingException(
                    "Field binding failed for " + identifier, e);
        }
    }

    private Object getFieldValue(Object object, Field field)
            throws IllegalArgumentException, IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        return field.get(object);
    }

    /**
     * Converts the given identifier to a valid field name by stripping away
     * illegal character and setting the first letter of the name to lower case.
     *
     * @param identifier
     *            the identifier to be converted to field name
     * @return the field name corresponding the identifier
     */
    private static String asFieldName(String identifier) {
        if (identifier == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < identifier.length(); i++) {
            char character = identifier.charAt(i);
            if (Character.isJavaIdentifierPart(character)) {
                result.append(character);
            }
        }
        // lowercase first letter
        if (result.length() != 0 && Character.isLetter(result.charAt(0))) {
            result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        }
        return result.toString();
    }

    /**
     * Returns a list containing Field objects reflecting all the fields of the
     * class or interface represented by this Class object. The fields in
     * superclasses are excluded.
     *
     * @param searchClass
     *            the class to be scanned for fields
     * @return the list of fields in this class
     */
    protected static List<java.lang.reflect.Field> getFields(
            Class<?> searchClass) {
        List<java.lang.reflect.Field> memberFields = new ArrayList<>();

        memberFields.addAll(Arrays.asList(searchClass.getDeclaredFields()));
        return memberFields;
    }

    private static Logger getLogger() {
        return Logger.getLogger(FieldBinder.class.getName());
    }

}
