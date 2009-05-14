/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.data.Property;

/**
 * Factory for creating new Field-instances based on type, datasource and/or
 * context.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.1
 * @deprecated FieldFactory was split into two lighter interfaces in 6.0 Use
 *             FormFieldFactory or TableFieldFactory or both instead.
 */
@Deprecated
public interface FieldFactory extends FormFieldFactory, TableFieldFactory {

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

}