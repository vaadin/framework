/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.fieldbinder;

import java.io.Serializable;

import com.vaadin.ui.Field;

/**
 * Factory interface for creating new Field-instances based on the data type
 * that should be edited.
 * 
 * @author Vaadin Ltd.
 * @version @version@
 * @since 7.0
 */
public interface FieldBinderFieldFactory extends Serializable {
    /**
     * Creates a field based on the data type that we want to edit
     * 
     * @param dataType
     *            The type that we want to edit using the field
     * @return A field capable of editing the given type of data
     */
    Field<?> createField(Class<?> dataType);
}
