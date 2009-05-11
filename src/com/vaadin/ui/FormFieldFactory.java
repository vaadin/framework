package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.data.Item;

public interface FormFieldFactory extends Serializable {
    /**
     * Creates a field based on the item, property id and the component where
     * the Field will be placed in.
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
