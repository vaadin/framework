/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.ui;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;

/**
 * Factory for creating new Field-instances based on type, datasource and/or
 * context.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.1
 */
public interface FieldFactory {

    /**
     * Creates a field based on type of data.
     * 
     * @param type
     *                the type of data presented in field.
     * @param uiContext
     *                the component where the field is presented.
     * @return Field the field suitable for editing the specified data.
     * 
     */
    Field createField(Class type, Component uiContext);

    /**
     * Creates a field based on the property datasource.
     * 
     * @param property
     *                the property datasource.
     * @param uiContext
     *                the component where the field is presented.
     * @return Field the field suitable for editing the specified data.
     */
    Field createField(Property property, Component uiContext);

    /**
     * Creates a field based on the item and property id.
     * 
     * @param item
     *                the item where the property belongs to.
     * @param propertyId
     *                the Id of the property.
     * @param uiContext
     *                the component where the field is presented.
     * @return Field the field suitable for editing the specified data.
     */
    Field createField(Item item, Object propertyId, Component uiContext);

    /**
     * Creates a field based on the container item id and property id.
     * 
     * @param container
     *                the Container where the property belongs to.
     * @param itemId
     *                the item Id.
     * @param propertyId
     *                the Id of the property.
     * @param uiContext
     *                the component where the field is presented.
     * @return Field the field suitable for editing the specified data.
     */
    Field createField(Container container, Object itemId, Object propertyId,
            Component uiContext);

}