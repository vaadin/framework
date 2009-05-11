/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Factory for creating new Field-instances based on type, datasource and/or
 * context.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.1
 */
public interface FieldFactory extends Serializable {

    /**
     * Creates a field based on type of data.
     * 
     * @param type
     *            the type of data presented in field.
     * @param uiContext
     *            the component where the field is presented.
     * @return Field the field suitable for editing the specified data.
     * 
     */
    Field createField(Class type, Component uiContext);

    /**
     * Creates a field based on the property datasource.
     * 
     * @param property
     *            the property datasource.
     * @param uiContext
     *            the component where the field is presented.
     * @return Field the field suitable for editing the specified data.
     */
    Field createField(Property property, Component uiContext);

    /**
     * Creates a field based on the item and property id.
     * 
     * @param item
     *            the item where the property belongs to.
     * @param propertyId
     *            the Id of the property.
     * @param uiContext
     *            the component where the field is presented.
     * @return Field the field suitable for editing the specified data.
     */
    Field createField(Item item, Object propertyId, Component uiContext);

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