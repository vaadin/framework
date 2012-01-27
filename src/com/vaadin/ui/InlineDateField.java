/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Date;

import com.vaadin.data.Property;
import com.vaadin.terminal.gwt.client.ui.VDateFieldCalendar;

/**
 * <p>
 * A date entry component, which displays the actual date selector inline.
 * 
 * </p>
 * 
 * @see DateField
 * @see PopupDateField
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
@ClientWidget(VDateFieldCalendar.class)
public class InlineDateField extends DateField {

    public InlineDateField() {
        super();
    }

    public InlineDateField(Property dataSource) throws IllegalArgumentException {
        super(dataSource);
    }

    public InlineDateField(String caption, Date value) {
        super(caption, value);
    }

    public InlineDateField(String caption, Property dataSource) {
        super(caption, dataSource);
    }

    public InlineDateField(String caption) {
        super(caption);
    }

}
