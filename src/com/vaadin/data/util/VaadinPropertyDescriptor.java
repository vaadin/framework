/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util;

import java.io.Serializable;

import com.vaadin.data.Property;

/**
 * Property descriptor that can create a property instance for a bean.
 * 
 * Used by {@link BeanItem} and {@link AbstractBeanContainer} to keep track of
 * the set of properties of items.
 * 
 * @param <BT>
 *            bean type
 * 
 * @since 6.6
 */
public interface VaadinPropertyDescriptor<BT> extends Serializable {
    /**
     * Returns the name of the property.
     * 
     * @return
     */
    public String getName();

    /**
     * Returns the type of the property.
     * 
     * @return Class<?>
     */
    public Class<?> getPropertyType();

    /**
     * Creates a new {@link Property} instance for this property for a bean.
     * 
     * @param bean
     * @return
     */
    public Property createProperty(BT bean);
}