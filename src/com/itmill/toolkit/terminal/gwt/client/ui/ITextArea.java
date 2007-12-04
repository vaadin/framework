/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * This class represents a multiline textfield (textarea).
 * 
 * @author IT Mill Ltd.
 * 
 */
public class ITextArea extends ITextField {
    public static final String CLASSNAME = "i-textarea";

    public ITextArea() {
        super(DOM.createTextArea());
        setStyleName(CLASSNAME);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Call parent renderer explicitly
        super.updateFromUIDL(uidl, client);

        if (uidl.hasAttribute("rows")) {
            setRows(new Integer(uidl.getStringAttribute("rows")).intValue());
        }
    }

}
