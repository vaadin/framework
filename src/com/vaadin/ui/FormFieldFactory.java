/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.data.Item;

/**
 * Factory interface for creating new Field-instances based on {@link Item},
 * property id and uiContext (the component responsible for displaying fields).
 * Currently this interface is used by {@link Form}, but might later be used by
 * some other components for {@link Field} generation.
 * 
 * <p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 6.0
 * @see TableFieldFactory
 */
public interface FormFieldFactory extends Serializable {
    /**
     * Creates a field based on the item, property id and the component (most
     * commonly {@link Form}) where the Field will be presented.
     * 
     * @param item
     *            the item where the property belongs to.
     * @param propertyId
     *            the Id of the property.
     * @param uiContext
     *            the component where the field is presented, most commonly this
     *            is {@link Form}. uiContext will not necessary be the parent
     *            component of the field, but the one that is responsible for
     *            creating it.
     * @return Field the field suitable for editing the specified data.
     */
    Field createField(Item item, Object propertyId, Component uiContext);
}
