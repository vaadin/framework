/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.Date;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;

/**
 * Default implementation of the the following Field types are used by default:
 * <p>
 * <b>Boolean</b>: Button(switchMode:true).<br/> <b>Date</b>:
 * DateField(resolution: day).<br/> <b>Item</b>: Form. <br/> <b>default field
 * type</b>: TextField.
 * <p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.1
 */

public class BaseFieldFactory implements FieldFactory {

    /**
     * Creates the field based on type of data.
     * 
     * 
     * @param type
     *                the type of data presented in field.
     * @param uiContext
     *                the context where the Field is presented.
     * 
     * @see com.itmill.toolkit.ui.FieldFactory#createField(Class, Component)
     */
    public Field createField(Class type, Component uiContext) {
        // Null typed properties can not be edited
        if (type == null) {
            return null;
        }

        // Item field
        if (Item.class.isAssignableFrom(type)) {
            return new Form();
        }

        // Date field
        if (Date.class.isAssignableFrom(type)) {
            final DateField df = new DateField();
            df.setResolution(DateField.RESOLUTION_DAY);
            return df;
        }

        // Boolean field
        if (Boolean.class.isAssignableFrom(type)) {
            final Button button = new Button();
            button.setSwitchMode(true);
            button.setImmediate(false);
            return button;
        }

        // Nested form is used by default
        return new TextField();
    }

    /**
     * Creates the field based on the datasource property.
     * 
     * @see com.itmill.toolkit.ui.FieldFactory#createField(Property, Component)
     */
    public Field createField(Property property, Component uiContext) {
        if (property != null) {
            return createField(property.getType(), uiContext);
        } else {
            return null;
        }
    }

    /**
     * Creates the field based on the item and property id.
     * 
     * @see com.itmill.toolkit.ui.FieldFactory#createField(Item, Object,
     *      Component)
     */
    public Field createField(Item item, Object propertyId, Component uiContext) {
        if (item != null && propertyId != null) {
            final Field f = createField(item.getItemProperty(propertyId),
                    uiContext);
            if (f instanceof AbstractComponent) {
                ((AbstractComponent) f).setCaption(propertyId.toString());
            }
            return f;
        } else {
            return null;
        }
    }

    /**
     * @see com.itmill.toolkit.ui.FieldFactory#createField(com.itmill.toolkit.data.Container,
     *      java.lang.Object, java.lang.Object, com.itmill.toolkit.ui.Component)
     */
    public Field createField(Container container, Object itemId,
            Object propertyId, Component uiContext) {
        return createField(container.getContainerProperty(itemId, propertyId),
                uiContext);
    }

}
