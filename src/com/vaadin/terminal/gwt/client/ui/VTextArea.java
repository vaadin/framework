/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * This class represents a multiline textfield (textarea).
 * 
 * TODO consider replacing this with a RichTextArea based implementation. IE
 * does not support CSS height for textareas in Strict mode :-(
 * 
 * @author IT Mill Ltd.
 * 
 */
public class VTextArea extends VTextField {
    public static final String CLASSNAME = "i-textarea";

    public VTextArea() {
        super(DOM.createTextArea());
        setStyleName(CLASSNAME);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Call parent renderer explicitly
        super.updateFromUIDL(uidl, client);

        if (uidl.hasAttribute("rows")) {
            setRows(new Integer(uidl.getStringAttribute("rows")).intValue());
        }

        if (getMaxLength() >= 0) {
            sinkEvents(Event.ONKEYPRESS);
        }
    }

    public void setRows(int rows) {
        setRows(getElement(), rows);
    }

    private native void setRows(Element e, int r)
    /*-{
    try {
        if(e.tagName.toLowerCase() == "textarea")
                e.rows = r;
    } catch (e) {}
    }-*/;

    @Override
    public void onBrowserEvent(Event event) {
        if (getMaxLength() >= 0 && event.getTypeInt() == Event.ONKEYPRESS) {
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    if (getText().length() > getMaxLength()) {
                        setText(getText().substring(0, getMaxLength()));
                    }
                }
            });
        }
        super.onBrowserEvent(event);
    }

}
