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
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
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
        ChangeListener, FocusListener, ContainerResizedListener {

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
    private float proportionalHeight = -1;
    private float proportionalWidth = -1;
    private int extraHorizontalPixels = -1;
    private int extraVerticalPixels = -1;

    public ITextField() {
        this(DOM.createInputText());
    }

    protected ITextField(Element node) {
        super(node);
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

    public void setHeight(String height) {
        if (height != null && height.indexOf("%") > 0) {
            // special handling for proportional height
            proportionalHeight = Float.parseFloat(height.substring(0, height
                    .indexOf("%"))) / 100;
            iLayout();
        } else {
            super.setHeight(height);
            proportionalHeight = -1;
        }
    }

    public void setWidth(String width) {
        if (width != null && width.indexOf("%") > 0) {
            // special handling for proportional w
            proportionalWidth = Float.parseFloat(width.substring(0, width
                    .indexOf("%"))) / 100;
            iLayout();
        } else {
            super.setWidth(width);
            proportionalWidth = -1;
        }
    }

    public void iLayout() {
        if (proportionalWidth >= 0) {
            int availPixels = (int) (DOM.getElementPropertyInt(DOM
                    .getParent(getElement()), "clientWidth") * proportionalWidth);
            availPixels -= getExtraHorizontalPixels();
            if (availPixels >= 0) {
                super.setWidth(availPixels + "px");
            }
        }
        if (proportionalHeight >= 0) {
            int availPixels = (int) (DOM.getElementPropertyInt(DOM
                    .getParent(getElement()), "clientHeight") * proportionalHeight);
            availPixels -= getExtraVerticalPixels();
            super.setHeight(availPixels + "px");
        }
    }

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
        if (BrowserInfo.get().isIE()) {
            // IE just don't accept 0 margin for textarea #2058
            extraVerticalPixels += 2;
        }

        DOM.removeChild(DOM.getParent(getElement()), clone);
    }

}
