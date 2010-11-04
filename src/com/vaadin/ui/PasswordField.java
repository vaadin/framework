package com.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.terminal.gwt.client.ui.VPasswordField;

/**
 * A field that is used to enter secret text information like passwords. The
 * clear text is not displayed in the screen.
 */
@ClientWidget(VPasswordField.class)
@SuppressWarnings("serial")
public class PasswordField extends TextField {

    /**
     * Constructs an empty PasswordField.
     */
    public PasswordField() {
        super();
    }

    /**
     * Constructs a PasswordField with given property data source.
     * 
     * @param dataSource
     *            the property dato source for the field
     */
    public PasswordField(Property dataSource) {
        super(dataSource);
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
        super(caption, dataSource);
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
        super(caption, value);
    }

    /**
     * Constructs a PasswordField with given caption.
     * 
     * @param caption
     *            the caption for the field
     */
    public PasswordField(String caption) {
        super(caption);
    }

}
