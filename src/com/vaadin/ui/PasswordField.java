package com.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.terminal.gwt.client.ui.VPasswordField;

@ClientWidget(VPasswordField.class)
@SuppressWarnings("serial")
public class PasswordField extends TextField {

    public PasswordField() {
        super();
    }

    public PasswordField(Property dataSource) {
        super(dataSource);
    }

    public PasswordField(String caption, Property dataSource) {
        super(caption, dataSource);
    }

    public PasswordField(String caption, String value) {
        super(caption, value);
    }

    public PasswordField(String caption) {
        super(caption);
    }

}
