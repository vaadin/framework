package com.vaadin.data.util;

import com.vaadin.data.Property;

/**
 * Property descriptor that is able to create nested property instances for a
 * bean.
 * 
 * The property is specified in the dotted notation, e.g. "address.street", and
 * can contain multiple levels of nesting.
 * 
 * @param <BT>
 *            bean type
 * 
 * @since 6.6
 */
public class NestedPropertyDescriptor<BT> implements
        VaadinPropertyDescriptor<BT> {

    private final String name;
    private final Class<?> propertyType;

    /**
     * Creates a property descriptor that can create MethodProperty instances to
     * access the underlying bean property.
     * 
     * The property is only validated when trying to access its value.
     * 
     * @param name
     *            of the property in a dotted path format, e.g. "address.street"
     * @param propertyType
     *            type (class) of the property
     */
    public NestedPropertyDescriptor(String name, Class<?> propertyType) {
        this.name = name;
        this.propertyType = propertyType;
    }

    public String getName() {
        return name;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public Property createProperty(BT bean) {
        return NestedMethodProperty.buildNestedMethodProperty(bean, name);
    }

}
