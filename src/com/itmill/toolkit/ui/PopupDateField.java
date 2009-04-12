/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.Date;

import com.itmill.toolkit.data.Property;

/**
 * <p>
 * A date entry component, which displays the actual date selector as a popup.
 * 
 * </p>
 * 
 * @see DateField
 * @see InlineDateField
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
@SuppressWarnings("serial")
public class PopupDateField extends DateField {

    public PopupDateField() {
        super();
        type = TYPE_POPUP;
    }

    public PopupDateField(Property dataSource) throws IllegalArgumentException {
        super(dataSource);
        type = TYPE_POPUP;
    }

    public PopupDateField(String caption, Date value) {
        super(caption, value);
        type = TYPE_POPUP;
    }

    public PopupDateField(String caption, Property dataSource) {
        super(caption, dataSource);
        type = TYPE_POPUP;
    }

    public PopupDateField(String caption) {
        super(caption);
        type = TYPE_POPUP;
    }

}
