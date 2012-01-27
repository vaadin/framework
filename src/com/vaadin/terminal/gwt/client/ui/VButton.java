/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.communication.ClientToServerRpc;
import com.vaadin.terminal.gwt.client.communication.ClientToServerRpc.InitializableClientToServerRpc;

public class VButton extends FocusWidget implements ClickHandler, FocusHandler,
        BlurHandler {

    public static final String CLASSNAME = "v-button";
    private static final String CLASSNAME_PRESSED = "v-pressed";

    /**
     * RPC interface for calls from client to server.
     * 
     * @since 7.0
     */
    public interface ButtonClientToServerRpc extends ClientToServerRpc {
        /**
         * Button click event.
         * 
         * @param mouseEventDetails
         *            serialized mouse event details
         */
        public void click(String mouseEventDetails);

        /**
         * Indicate to the server that the client has disabled the button as a
         * result of a click.
         */
        public void disableOnClick();
    }

    public static final String ATTR_DISABLE_ON_CLICK = "dc";

    // mouse movement is checked before synthesizing click event on mouseout
    protected static int MOVE_THRESHOLD = 3;
    protected int mousedownX = 0;
    protected int mousedownY = 0;

    protected String paintableId;

    protected ApplicationConnection client;

    protected final Element wrapper = DOM.createSpan();

    protected Element errorIndicatorElement;

    protected final Element captionElement = DOM.createSpan();

    protected Icon icon;

    /**
     * Helper flag to handle special-case where the button is moved from under
     * mouse while clicking it. In this case mouse leaves the button without
     * moving.
     */
    protected boolean clickPending;

    private boolean enabled = true;

    private int tabIndex = 0;

    protected boolean disableOnClick = false;

    /*
     * BELOW PRIVATE MEMBERS COPY-PASTED FROM GWT CustomButton
     */

    /**
     * If <code>true</code>, this widget is capturing with the mouse held down.
     */
    private boolean isCapturing;

    /**
     * If <code>true</code>, this widget has focus with the space bar down.
     */
    private boolean isFocusing;

    /**
     * Used to decide whether to allow clicks to propagate up to the superclass
     * or container elements.
     */
    private boolean disallowNextClick = false;
    private boolean isHovering;

    protected HandlerRegistration focusHandlerRegistration;
    protected HandlerRegistration blurHandlerRegistration;

    protected int clickShortcut = 0;
    private ButtonClientToServerRpc buttonRpcProxy;

    public VButton() {
        super(DOM.createDiv());
        setTabIndex(0);
        sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.FOCUSEVENTS
                | Event.KEYEVENTS);
        sinkEvents(VTooltip.TOOLTIP_EVENTS);

        setStyleName(CLASSNAME);

        // Add a11y role "button"
        Accessibility.setRole(getElement(), Accessibility.ROLE_BUTTON);

        wrapper.setClassName(getStylePrimaryName() + "-wrap");
        getElement().appendChild(wrapper);
        captionElement.setClassName(getStylePrimaryName() + "-caption");
        wrapper.appendChild(captionElement);

        addClickHandler(this);
    }

    public void setText(String text) {
        captionElement.setInnerText(text);
    }

    @SuppressWarnings("deprecation")
    @Override
    /*
     * Copy-pasted from GWT CustomButton, some minor modifications done:
     * 
     * -for IE/Opera added CLASSNAME_PRESSED
     * 
     * -event.preventDefault() commented from ONMOUSEDOWN (Firefox won't apply
     * :active styles if it is present)
     * 
     * -Tooltip event handling added
     * 
     * -onload event handler added (for icon handling)
     */
    public void onBrowserEvent(Event event) {
        if (client != null) {
            client.handleWidgetTooltipEvent(event, this);
        }
        if (DOM.eventGetType(event) == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
        }
        // Should not act on button if disabled.
        if (!isEnabled()) {
            // This can happen when events are bubbled up from non-disabled
            // children
            return;
        }

        int type = DOM.eventGetType(event);
        switch (type) {
        case Event.ONCLICK:
            // If clicks are currently disallowed, keep it from bubbling or
            // being passed to the superclass.
            if (disallowNextClick) {
                event.stopPropagation();
                disallowNextClick = false;
                return;
            }
            break;
        case Event.ONMOUSEDOWN:
            if (DOM.isOrHasChild(getElement(), DOM.eventGetTarget(event))) {
                // This was moved from mouseover, which iOS sometimes skips.
                // We're certainly hovering at this point, and we don't actually
                // need that information before this point.
                setHovering(true);
            }
            if (event.getButton() == Event.BUTTON_LEFT) {
                // save mouse position to detect movement before synthesizing
                // event later
                mousedownX = event.getClientX();
                mousedownY = event.getClientY();

                disallowNextClick = true;
                clickPending = true;
                setFocus(true);
                DOM.setCapture(getElement());
                isCapturing = true;
                // Prevent dragging (on some browsers);
                // DOM.eventPreventDefault(event);
                if (BrowserInfo.get().isIE() || BrowserInfo.get().isOpera()) {
                    addStyleName(CLASSNAME_PRESSED);
                }
            }
            break;
        case Event.ONMOUSEUP:
            if (isCapturing) {
                isCapturing = false;
                DOM.releaseCapture(getElement());
                if (isHovering() && event.getButton() == Event.BUTTON_LEFT) {
                    // Click ok
                    disallowNextClick = false;
                }
                if (BrowserInfo.get().isIE() || BrowserInfo.get().isOpera()) {
                    removeStyleName(CLASSNAME_PRESSED);
                }
            }
            break;
        case Event.ONMOUSEMOVE:
            clickPending = false;
            if (isCapturing) {
                // Prevent dragging (on other browsers);
                DOM.eventPreventDefault(event);
            }
            break;
        case Event.ONMOUSEOUT:
            Element to = event.getRelatedTarget();
            if (getElement().isOrHasChild(DOM.eventGetTarget(event))
                    && (to == null || !getElement().isOrHasChild(to))) {
                if (clickPending
                        && Math.abs(mousedownX - event.getClientX()) < MOVE_THRESHOLD
                        && Math.abs(mousedownY - event.getClientY()) < MOVE_THRESHOLD) {
                    onClick();
                    break;
                }
                clickPending = false;
                if (isCapturing) {
                }
                setHovering(false);
                if (BrowserInfo.get().isIE() || BrowserInfo.get().isOpera()) {
                    removeStyleName(CLASSNAME_PRESSED);
                }
            }
            break;
        case Event.ONBLUR:
            if (isFocusing) {
                isFocusing = false;
            }
            break;
        case Event.ONLOSECAPTURE:
            if (isCapturing) {
                isCapturing = false;
            }
            break;
        }

        super.onBrowserEvent(event);

        // Synthesize clicks based on keyboard events AFTER the normal key
        // handling.
        if ((event.getTypeInt() & Event.KEYEVENTS) != 0) {
            switch (type) {
            case Event.ONKEYDOWN:
                if (event.getKeyCode() == 32 /* space */) {
                    isFocusing = true;
                    event.preventDefault();
                }
                break;
            case Event.ONKEYUP:
                if (isFocusing && event.getKeyCode() == 32 /* space */) {
                    isFocusing = false;

                    /*
                     * If click shortcut is space then the shortcut handler will
                     * take care of the click.
                     */
                    if (clickShortcut != 32 /* space */) {
                        onClick();
                    }

                    event.preventDefault();
                }
                break;
            case Event.ONKEYPRESS:
                if (event.getKeyCode() == KeyCodes.KEY_ENTER) {

                    /*
                     * If click shortcut is enter then the shortcut handler will
                     * take care of the click.
                     */
                    if (clickShortcut != KeyCodes.KEY_ENTER) {
                        onClick();
                    }

                    event.preventDefault();
                }
                break;
            }
        }
    }

    final void setHovering(boolean hovering) {
        if (hovering != isHovering()) {
            isHovering = hovering;
        }
    }

    final boolean isHovering() {
        return isHovering;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
     * .dom.client.ClickEvent)
     */
    public void onClick(ClickEvent event) {
        if (paintableId == null || client == null) {
            return;
        }
        if (BrowserInfo.get().isSafari()) {
            VButton.this.setFocus(true);
        }
        if (disableOnClick) {
            setEnabled(false);
            getButtonRpcProxy().disableOnClick();
        }

        // Add mouse details
        MouseEventDetails details = new MouseEventDetails(
                event.getNativeEvent(), getElement());
        getButtonRpcProxy().click(details.serialize());

        clickPending = false;
    }

    protected ButtonClientToServerRpc getButtonRpcProxy() {
        if (null == buttonRpcProxy) {
            buttonRpcProxy = GWT.create(ButtonClientToServerRpc.class);
            ((InitializableClientToServerRpc) buttonRpcProxy).initRpc(
                    paintableId, client);
        }
        return buttonRpcProxy;
    }

    /*
     * ALL BELOW COPY-PASTED FROM GWT CustomButton
     */

    /**
     * Called internally when the user finishes clicking on this button. The
     * default behavior is to fire the click event to listeners. Subclasses that
     * override {@link #onClickStart()} should override this method to restore
     * the normal widget display.
     * <p>
     * To add custom code for a click event, override
     * {@link #onClick(ClickEvent)} instead of this.
     */
    protected void onClick() {
        // Allow the click we're about to synthesize to pass through to the
        // superclass and containing elements. Element.dispatchEvent() is
        // synchronous, so we simply set and clear the flag within this method.

        disallowNextClick = false;

        // Mouse coordinates are not always available (e.g., when the click is
        // caused by a keyboard event).
        NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false,
                false, false, false);
        getElement().dispatchEvent(evt);
    }

    /**
     * Sets whether this button is enabled.
     * 
     * @param enabled
     *            <code>true</code> to enable the button, <code>false</code> to
     *            disable it
     */

    @Override
    public final void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            this.enabled = enabled;
            if (!enabled) {
                cleanupCaptureState();
                Accessibility.removeState(getElement(),
                        Accessibility.STATE_PRESSED);
                super.setTabIndex(-1);
                addStyleName(ApplicationConnection.DISABLED_CLASSNAME);
            } else {
                Accessibility.setState(getElement(),
                        Accessibility.STATE_PRESSED, "false");
                super.setTabIndex(tabIndex);
                removeStyleName(ApplicationConnection.DISABLED_CLASSNAME);
            }
        }
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public final void setTabIndex(int index) {
        super.setTabIndex(index);
        tabIndex = index;
    }

    /**
     * Resets internal state if this button can no longer service events. This
     * can occur when the widget becomes detached or disabled.
     */
    private void cleanupCaptureState() {
        if (isCapturing || isFocusing) {
            DOM.releaseCapture(getElement());
            isCapturing = false;
            isFocusing = false;
        }
    }

    private static native int getHorizontalBorderAndPaddingWidth(Element elem)
    /*-{
        // THIS METHOD IS ONLY USED FOR INTERNET EXPLORER, IT DOESN'T WORK WITH OTHERS
    	
    	var convertToPixel = function(elem, value) {
    	    // From the awesome hack by Dean Edwards
            // http://erik.eae.net/archives/2007/07/27/18.54.15/#comment-102291

            // Remember the original values
            var left = elem.style.left, rsLeft = elem.runtimeStyle.left;

            // Put in the new values to get a computed value out
            elem.runtimeStyle.left = elem.currentStyle.left;
            elem.style.left = value || 0;
            var ret = elem.style.pixelLeft;

            // Revert the changed values
            elem.style.left = left;
            elem.runtimeStyle.left = rsLeft;
            
            return ret;
    	}
    	
     	var ret = 0;

        var sides = ["Right","Left"];
        for(var i=0; i<2; i++) {
            var side = sides[i];
            var value;
            // Border -------------------------------------------------------
            if(elem.currentStyle["border"+side+"Style"] != "none") {
                value = elem.currentStyle["border"+side+"Width"];
                if ( !/^\d+(px)?$/i.test( value ) && /^\d/.test( value ) ) {
                    ret += convertToPixel(elem, value);
                } else if(value.length > 2) {
                    ret += parseInt(value.substr(0, value.length-2));
                }
            }
                    
            // Padding -------------------------------------------------------
            value = elem.currentStyle["padding"+side];
            if ( !/^\d+(px)?$/i.test( value ) && /^\d/.test( value ) ) {
                ret += convertToPixel(elem, value);
            } else if(value.length > 2) {
                ret += parseInt(value.substr(0, value.length-2));
            }
        }

    	return ret;
    }-*/;

    public void onFocus(FocusEvent arg0) {
        client.updateVariable(paintableId, EventId.FOCUS, "", true);
    }

    public void onBlur(BlurEvent arg0) {
        client.updateVariable(paintableId, EventId.BLUR, "", true);
    }

    public Widget getWidgetForPaintable() {
        return this;
    }

}
