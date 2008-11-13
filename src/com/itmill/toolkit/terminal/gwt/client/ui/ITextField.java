/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.ITooltip;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

/**
 * This class represents a basic text input field with one row.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class ITextField extends TextBoxBase implements Paintable, Field,
        ChangeListener, FocusListener {

    /**
     * The input node CSS classname.
     */
    public static final String CLASSNAME = "i-textfield";
    /**
     * This CSS classname is added to the input node on hover.
     */
    public static final String CLASSNAME_FOCUS = "focus";

    protected String id;

    protected ApplicationConnection client;

    private String valueBeforeEdit = null;

    private boolean immediate = false;
    private int extraHorizontalPixels = -1;
    private int extraVerticalPixels = -1;

    public ITextField() {
        this(DOM.createInputText());
    }

    protected ITextField(Element node) {
        super(node);
        if (BrowserInfo.get().isIE()) {
            // Fixes IE margin problem (#2058)
            DOM.setStyleAttribute(node, "marginTop", "-1px");
            DOM.setStyleAttribute(node, "marginBottom", "-1px");
        }
        setStyleName(CLASSNAME);
        addChangeListener(this);
        addFocusListener(this);
        sinkEvents(ITooltip.TOOLTIP_EVENTS);
    }

    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (client != null) {
            client.handleTooltipEvent(event, this);
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        id = uidl.getId();

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        if (uidl.getBooleanAttribute("readonly")) {
            setReadOnly(true);
        } else {
            setReadOnly(false);
        }

        immediate = uidl.getBooleanAttribute("immediate");

        if (uidl.hasAttribute("cols")) {
            setColumns(new Integer(uidl.getStringAttribute("cols")).intValue());
        }

        setText(uidl.getStringVariable("text"));
        valueBeforeEdit = uidl.getStringVariable("text");
    }

    public void onChange(Widget sender) {
        if (client != null && id != null) {
            String newText = getText();
            if (newText != null && !newText.equals(valueBeforeEdit)) {
                client.updateVariable(id, "text", getText(), immediate);
                valueBeforeEdit = newText;
            }
        }
    }

    private static ITextField focusedTextField;

    public static void flushChangesFromFocusedTextField() {
        if (focusedTextField != null) {
            focusedTextField.onChange(null);
        }
    }

    public void onFocus(Widget sender) {
        addStyleDependentName(CLASSNAME_FOCUS);
        focusedTextField = this;
    }

    public void onLostFocus(Widget sender) {
        removeStyleDependentName(CLASSNAME_FOCUS);
        focusedTextField = null;
        onChange(sender);
    }

    public void setColumns(int columns) {
        setColumns(getElement(), columns);
    }

    private native void setColumns(Element e, int c)
    /*-{
    try {
    	switch(e.tagName.toLowerCase()) {
    		case "input":
    			//e.size = c;
    			e.style.width = c+"em";
    			break;
    		case "textarea":
    			//e.cols = c;
    			e.style.width = c+"em";
    			break;
    		default:;
    	}
    } catch (e) {}
    }-*/;

    /**
     * @return space used by components paddings and borders
     */
    private int getExtraHorizontalPixels() {
        if (extraHorizontalPixels < 0) {
            detectExtraSizes();
        }
        return extraHorizontalPixels;
    }

    /**
     * @return space used by components paddings and borders
     */
    private int getExtraVerticalPixels() {
        if (extraVerticalPixels < 0) {
            detectExtraSizes();
        }
        return extraVerticalPixels;
    }

    /**
     * Detects space used by components paddings and borders. Used when
     * relational size are used.
     */
    private void detectExtraSizes() {
        Element clone = Util.cloneNode(getElement(), false);
        DOM.setElementAttribute(clone, "id", "");
        DOM.setStyleAttribute(clone, "visibility", "hidden");
        DOM.setStyleAttribute(clone, "position", "absolute");
        // due FF3 bug set size to 10px and later subtract it from extra pixels
        DOM.setStyleAttribute(clone, "width", "10px");
        DOM.setStyleAttribute(clone, "height", "10px");
        DOM.appendChild(DOM.getParent(getElement()), clone);
        extraHorizontalPixels = DOM.getElementPropertyInt(clone, "offsetWidth") - 10;
        extraVerticalPixels = DOM.getElementPropertyInt(clone, "offsetHeight") - 10;

        DOM.removeChild(DOM.getParent(getElement()), clone);
    }

    @Override
    public void setHeight(String height) {
        if (height.endsWith("px")) {
            int h = Integer.parseInt(height.substring(0, height.length() - 2));
            h -= getExtraVerticalPixels();
            if (h < 0) {
                h = 0;
            }

            super.setHeight(h + "px");
        } else {
            super.setHeight(height);
        }
    }

    @Override
    public void setWidth(String width) {
        if (width.endsWith("px")) {
            int w = Integer.parseInt(width.substring(0, width.length() - 2));
            w -= getExtraHorizontalPixels();
            if (w < 0) {
                w = 0;
            }

            super.setWidth(w + "px");
        } else {
            super.setWidth(width);
        }
    }

}
