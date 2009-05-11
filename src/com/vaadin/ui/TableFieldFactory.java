package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.data.Container;

public interface TableFieldFactory extends Serializable {
    /**
     * Creates a field based on the container item id and property id.
     * 
     * @param container
     *            the Container where the property belongs to.
     * @param itemId
     *            the item Id.
     * @param propertyId
     *            the Id of the property.
     * @param uiContext
     *            the component where the field is presented.
     * @return Field the field suitable for editing the specified data.
     */
    Field createField(Container container, Object itemId, Object propertyId,
            Component uiContext);

}
