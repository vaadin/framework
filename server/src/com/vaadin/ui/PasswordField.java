/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import com.vaadin.data.Property;

/**
 * A field that is used to enter secret text information like passwords. The
 * entered text is not displayed on the screen.
 */
public class PasswordField extends AbstractTextField {

    /**
     * Constructs an empty PasswordField.
     */
    public PasswordField() {
        setValue("");
    }

    /**
     * Constructs a PasswordField with given property data source.
     * 
     * @param dataSource
     *            the property data source for the field
     */
    public PasswordField(Property dataSource) {
        setPropertyDataSource(dataSource);
    }

    /**
     * Constructs a PasswordField with given caption and property data source.
     * 
     * @param caption
     *            the caption for the field
     * @param dataSource
     *            the property data source for the field
     */
    public PasswordField(String caption, Property dataSource) {
        this(dataSource);
        setCaption(caption);
    }

    /**
     * Constructs a PasswordField with given value and caption.
     * 
     * @param caption
     *            the caption for the field
     * @param value
     *            the value for the field
     */
    public PasswordField(String caption, String value) {
        setValue(value);
        setCaption(caption);
    }

    /**
     * Constructs a PasswordField with given caption.
     * 
     * @param caption
     *            the caption for the field
     */
    public PasswordField(String caption) {
        this();
        setCaption(caption);
    }
}
