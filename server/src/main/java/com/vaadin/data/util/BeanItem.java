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

package com.vaadin.data.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Property;

/**
 * A wrapper class for adding the Item interface to any Java Bean.
 *
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class BeanItem<BT> extends PropertysetItem {

    /**
     * The bean which this Item is based on.
     */
    private BT bean;

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> and adds all properties
     * of a Java Bean to it. The properties are identified by their respective
     * bean names.
     * </p>
     *
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     *
     * @param bean
     *            the Java Bean to copy properties from.
     *
     */
    public BeanItem(BT bean) {
        this(bean, (Class<BT>) bean.getClass());
    }

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> and adds all properties
     * of a Java Bean to it. The properties are identified by their respective
     * bean names.
     * </p>
     *
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     *
     * @since 7.4
     *
     * @param bean
     *            the Java Bean to copy properties from.
     * @param beanClass
     *            class of the {@code bean}
     *
     */
    public BeanItem(BT bean, Class<BT> beanClass) {
        this(bean, getPropertyDescriptors(beanClass));
    }

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> using a pre-computed set
     * of properties. The properties are identified by their respective bean
     * names.
     * </p>
     *
     * @param bean
     *            the Java Bean to copy properties from.
     * @param propertyDescriptors
     *            pre-computed property descriptors
     */
    BeanItem(BT bean,
            Map<String, VaadinPropertyDescriptor<BT>> propertyDescriptors) {

        this.bean = bean;

        for (VaadinPropertyDescriptor<BT> pd : propertyDescriptors.values()) {
            addItemProperty(pd.getName(), pd.createProperty(bean));
        }
    }

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> and adds all listed
     * properties of a Java Bean to it - in specified order. The properties are
     * identified by their respective bean names.
     * </p>
     *
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     *
     * @param bean
     *            the Java Bean to copy properties from.
     * @param propertyIds
     *            id of the property.
     */
    public BeanItem(BT bean, Collection<?> propertyIds) {

        this.bean = bean;

        // Create bean information
        LinkedHashMap<String, VaadinPropertyDescriptor<BT>> pds = getPropertyDescriptors(
                (Class<BT>) bean.getClass());

        // Add all the bean properties as MethodProperties to this Item
        for (Object id : propertyIds) {
            VaadinPropertyDescriptor<BT> pd = pds.get(id);
            if (pd != null) {
                addItemProperty(pd.getName(), pd.createProperty(bean));
            }
        }

    }

    /**
     * <p>
     * Creates a new instance of <code>BeanItem</code> and adds all listed
     * properties of a Java Bean to it - in specified order. The properties are
     * identified by their respective bean names.
     * </p>
     *
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     *
     * @param bean
     *            the Java Bean to copy properties from.
     * @param propertyIds
     *            ids of the properties.
     */
    public BeanItem(BT bean, String... propertyIds) {
        this(bean, Arrays.asList(propertyIds));
    }

    /**
     * <p>
     * Perform introspection on a Java Bean class to find its properties.
     * </p>
     *
     * <p>
     * Note : This version only supports introspectable bean properties and
     * their getter and setter methods. Stand-alone <code>is</code> and
     * <code>are</code> methods are not supported.
     * </p>
     *
     * @param beanClass
     *            the Java Bean class to get properties for.
     * @return an ordered map from property names to property descriptors
     */
    static <BT> LinkedHashMap<String, VaadinPropertyDescriptor<BT>> getPropertyDescriptors(
            final Class<BT> beanClass) {
        final LinkedHashMap<String, VaadinPropertyDescriptor<BT>> pdMap = new LinkedHashMap<String, VaadinPropertyDescriptor<BT>>();

        // Try to introspect, if it fails, we just have an empty Item
        try {
            List<PropertyDescriptor> propertyDescriptors = BeanUtil
                    .getBeanPropertyDescriptor(beanClass);

            // Add all the bean properties as MethodProperties to this Item
            // later entries on the list overwrite earlier ones
            for (PropertyDescriptor pd : propertyDescriptors) {
                final Method getMethod = pd.getReadMethod();
                if ((getMethod != null)
                        && getMethod.getDeclaringClass() != Object.class) {
                    VaadinPropertyDescriptor<BT> vaadinPropertyDescriptor = new MethodPropertyDescriptor<BT>(
                            pd.getName(), pd.getPropertyType(),
                            pd.getReadMethod(), pd.getWriteMethod());
                    pdMap.put(pd.getName(), vaadinPropertyDescriptor);
                }
            }
        } catch (final java.beans.IntrospectionException ignored) {
        }

        return pdMap;
    }

    /**
     * Expands nested bean properties by replacing a top-level property with
     * some or all of its sub-properties. The expansion is not recursive.
     *
     * @param propertyId
     *            property id for the property whose sub-properties are to be
     *            expanded,
     * @param subPropertyIds
     *            sub-properties to expand, all sub-properties are expanded if
     *            not specified
     */
    public void expandProperty(String propertyId, String... subPropertyIds) {
        Set<String> subPropertySet = new HashSet<String>(
                Arrays.asList(subPropertyIds));

        if (0 == subPropertyIds.length) {
            // Enumerate all sub-properties
            Class<?> propertyType = getItemProperty(propertyId).getType();
            Map<String, ?> pds = getPropertyDescriptors(propertyType);
            subPropertySet.addAll(pds.keySet());
        }

        for (String subproperty : subPropertySet) {
            String qualifiedPropertyId = propertyId + "." + subproperty;
            addNestedProperty(qualifiedPropertyId);
        }

        removeItemProperty(propertyId);
    }

    /**
     * Adds a nested property to the item. The property must not exist in the
     * item already and must of form "field1.field2" where field2 is a field in
     * the object referenced to by field1. If an intermediate property returns
     * null, the property will return a null value
     *
     * @param nestedPropertyId
     *            property id to add.
     */
    public void addNestedProperty(String nestedPropertyId) {
        addItemProperty(nestedPropertyId,
                new NestedMethodProperty<Object>(getBean(), nestedPropertyId));
    }

    /**
     * Gets the underlying JavaBean object.
     *
     * @return the bean object.
     */
    public BT getBean() {
        return bean;
    }

    /**
     * Changes the Java Bean this item is based on.
     * <p>
     * This will cause any existing properties to be re-mapped to the new bean.
     * Any added custom properties which are not of type {@link MethodProperty}
     * or {@link NestedMethodProperty} will not be updated to reflect the change
     * of bean.
     * <p>
     * Changing the bean will fire value change events for all properties of
     * type {@link MethodProperty} or {@link NestedMethodProperty}.
     * 
     * @param bean
     *            The new bean to use for this item, not <code>null</code>
     * @since 7.7.7
     */
    public void setBean(BT bean) {
        if (bean == null) {
            throw new IllegalArgumentException("Bean cannot be null");
        }

        if (getBean().getClass() != bean.getClass()) {
            throw new IllegalArgumentException(
                    "The new bean class " + bean.getClass().getName()
                            + " does not match the old bean class "
                            + getBean().getClass());
        }

        // Remap properties
        for (Object propertyId : getItemPropertyIds()) {
            Property p = getItemProperty(propertyId);
            if (p instanceof MethodProperty) {
                MethodProperty mp = (MethodProperty) p;
                assert (mp.getInstance() == getBean());
                mp.setInstance(bean);
            } else if (p instanceof NestedMethodProperty) {
                NestedMethodProperty nmp = (NestedMethodProperty) p;
                assert (nmp.getInstance() == getBean());
                nmp.setInstance(bean);
            }
        }

        this.bean = bean;
    }
}
