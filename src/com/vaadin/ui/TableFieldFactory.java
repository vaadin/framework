/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.data.Container;

/**
 * Factory interface for creating new Field-instances based on Container
 * (datasource), item id, property id and uiContext (the component responsible
 * for displaying fields). Currently this interface is used by {@link Table},
 * but might later be used by some other components for {@link Field}
 * generation.
 * 
 * <p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 6.0
 * @see FormFieldFactory
 */
public interface TableFieldFactory extends Serializable {
    /**
     * Creates a field based on the Container, item id, property id and the
     * component responsible for displaying the field (most commonly
     * {@link Table}).
     * 
     * @param container
     *            the Container where the property belongs to.
     * @param itemId
     *            the item Id.
     * @param propertyId
     *            the Id of the property.
     * @param uiContext
     *            the component where the field is presented.
     * @return A field suitable for editing the specified data.
     */
    Field createField(Container container, Object itemId, Object propertyId,
            Component uiContext);

}
