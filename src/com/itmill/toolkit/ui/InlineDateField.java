/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.Date;

import com.itmill.toolkit.data.Property;

/**
 * <p>
 * A date entry component, which displays the actual date selector inline.
 * 
 * </p>
 * 
 * @see DateField
 * @see PopupDateField
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
public class InlineDateField extends DateField {

    public InlineDateField() {
        super();
        type = TYPE_INLINE;
    }

    public InlineDateField(Property dataSource) throws IllegalArgumentException {
        super(dataSource);
        type = TYPE_INLINE;
    }

    public InlineDateField(String caption, Date value) {
        super(caption, value);
        type = TYPE_INLINE;
    }

    public InlineDateField(String caption, Property dataSource) {
        super(caption, dataSource);
        type = TYPE_INLINE;
    }

    public InlineDateField(String caption) {
        super(caption);
        type = TYPE_INLINE;
    }

}
