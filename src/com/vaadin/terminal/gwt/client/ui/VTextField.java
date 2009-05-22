/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;

/**
 * This class represents a basic text input field with one row.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class VTextField extends TextBoxBase implements Paintable, Field,
        ChangeListener, FocusListener {

    /**
     * The input node CSS classname.
     */
    public static final String CLASSNAME = "v-textfield";
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
    private int maxLength = -1;

    private static final String CLASSNAME_PROMPT = "prompt";
    private static final String ATTR_INPUTPROMPT = "prompt";
    private String inputPrompt = null;
    private boolean prompting = false;

    public VTextField() {
        this(DOM.createInputText());
    }

    protected VTextField(Element node) {
        super(node);
        if (BrowserInfo.get().isIE()) {
            // Fixes IE margin problem (#2058)
            DOM.setStyleAttribute(node, "marginTop", "-1px");
            DOM.setStyleAttribute(node, "marginBottom", "-1px");
        }
        setStyleName(CLASSNAME);
        addChangeListener(this);
        addFocusListener(this);
        sinkEvents(VTooltip.TOOLTIP_EVENTS);
    }

    @Override
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

        inputPrompt = uidl.getStringAttribute(ATTR_INPUTPROMPT);

        setMaxLength(uidl.hasAttribute("maxLength") ? uidl
                .getIntAttribute("maxLength") : -1);

        immediate = uidl.getBooleanAttribute("immediate");

        if (uidl.hasAttribute("cols")) {
            setColumns(new Integer(uidl.getStringAttribute("cols")).intValue());
        }

        String text = uidl.getStringVariable("text");
        setPrompting(inputPrompt != null && focusedTextField != this
                && (text == null || text.equals("")));
        if (prompting) {
            setText(inputPrompt);
            addStyleDependentName(CLASSNAME_PROMPT);
        } else {
            setText(text);
            removeStyleDependentName(CLASSNAME_PROMPT);
        }
        valueBeforeEdit = uidl.getStringVariable("text");
    }

    private void setMaxLength(int newMaxLength) {
        if (newMaxLength > 0) {
            maxLength = newMaxLength;
            if (getElement().getTagName().toLowerCase().equals("textarea")) {
                // NOP no maxlength property for textarea
            } else {
                getElement().setPropertyInt("maxLength", maxLength);
            }
        } else if (maxLength != -1) {
            if (getElement().getTagName().toLowerCase().equals("textarea")) {
                // NOP no maxlength property for textarea
            } else {
                getElement().setAttribute("maxlength", "");
            }
            maxLength = -1;
        }

    }

    protected int getMaxLength() {
        return maxLength;
    }

    public void onChange(Widget sender) {
        if (client != null && id != null) {
            String newText = getText();
            if (!prompting && newText != null
                    && !newText.equals(valueBeforeEdit)) {
                client.updateVariable(id, "text", getText(), immediate);
                valueBeforeEdit = newText;
            }
        }
    }

    private static VTextField focusedTextField;

    public static void flushChangesFromFocusedTextField() {
        if (focusedTextField != null) {
            focusedTextField.onChange(null);
        }
    }

    public void onFocus(Widget sender) {
        addStyleDependentName(CLASSNAME_FOCUS);
        if (prompting) {
            setText("");
            removeStyleDependentName(CLASSNAME_PROMPT);
        }
        focusedTextField = this;
    }

    public void onLostFocus(Widget sender) {
        removeStyleDependentName(CLASSNAME_FOCUS);
        focusedTextField = null;
        String text = getText();
        setPrompting(inputPrompt != null && (text == null || "".equals(text)));
        if (prompting) {
            setText(inputPrompt);
            addStyleDependentName(CLASSNAME_PROMPT);
        }
        onChange(sender);
    }

    private void setPrompting(boolean prompting) {
        this.prompting = prompting;
        System.out.println("Prompting is now: " + prompting);
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
