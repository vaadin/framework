package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * This class represents a basic text input field with one row.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class ITextField extends TextBoxBase implements Paintable,
        ChangeListener, FocusListener {

    /**
     * The input node CSS classname.
     */
    public static final String CLASSNAME = "i-textfield";

    /**
     * This CSS classname is added to the input node on hover.
     */
    public static final String CLASSNAME_FOCUS = "i-textfield-focus";

    protected String id;

    protected ApplicationConnection client;

    private boolean immediate = false;

    public ITextField() {
        this(DOM.createInputText());
    }

    protected ITextField(Element node) {
        super(node);
        setStyleName(CLASSNAME);
        addChangeListener(this);
        addFocusListener(this);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        id = uidl.getId();

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        immediate = uidl.getBooleanAttribute("immediate");

        if (uidl.hasAttribute("cols")) {
            setColumns(new Integer(uidl.getStringAttribute("cols")).intValue());
        }

        setText(uidl.getStringVariable("text"));

    }

    public void onChange(Widget sender) {
        if (client != null && id != null) {
            client.updateVariable(id, "text", getText(), immediate);
        }
    }

    public void onFocus(Widget sender) {
        addStyleName(CLASSNAME_FOCUS);
    }

    public void onLostFocus(Widget sender) {
        removeStyleName(CLASSNAME_FOCUS);
    }

    public void setColumns(int columns) {
        setColumns(getElement(), columns);
    }

    public void setRows(int rows) {
        setRows(getElement(), rows);
    }

    private native void setColumns(Element e, int c) /*-{
                   try {
                   	switch(e.tagName.toLowerCase()) {
                   		case "input":
                   			e.size = c;
                   			break;
                   		case "textarea":
                   			e.cols = c;
                   			break;
                   		default:;
                   	}
                   } catch (e) {}
                   }-*/;

    private native void setRows(Element e, int r) /*-{
                   try {
                   	if(e.tagName.toLowerCase() == "textarea")
                   		e.rows = r;
                   } catch (e) {}
                   }-*/;
}
