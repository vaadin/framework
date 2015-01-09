/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client.ui;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusWidget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Util;
import com.vaadin.client.WidgetUtil;

public class VButton extends FocusWidget implements ClickHandler {

    public static final String CLASSNAME = "v-button";
    private static final String CLASSNAME_PRESSED = "v-pressed";

    // mouse movement is checked before synthesizing click event on mouseout
    protected static int MOVE_THRESHOLD = 3;
    protected int mousedownX = 0;
    protected int mousedownY = 0;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public final Element wrapper = DOM.createSpan();

    /** For internal use only. May be removed or replaced in the future. */
    public Element errorIndicatorElement;

    /** For internal use only. May be removed or replaced in the future. */
    public final Element captionElement = DOM.createSpan();

    /** For internal use only. May be removed or replaced in the future. */
    public Icon icon;

    /**
     * Helper flag to handle special-case where the button is moved from under
     * mouse while clicking it. In this case mouse leaves the button without
     * moving.
     */
    protected boolean clickPending;

    private boolean enabled = true;

    private int tabIndex = 0;

    /*
     * BELOW PRIVATE MEMBERS COPY-PASTED FROM GWT CustomButton
     */

    /**
     * If <code>true</code>, this widget is capturing with the mouse held down.
     */
    private boolean isCapturing;

    /**
     * If <code>true</code>, this widget has focus with the space bar down. This
     * means that we will get events when the button is released, but we should
     * trigger the button only if the button is still focused at that point.
     */
    private boolean isFocusing;

    /**
     * Used to decide whether to allow clicks to propagate up to the superclass
     * or container elements.
     */
    private boolean disallowNextClick = false;
    private boolean isHovering;

    /** For internal use only. May be removed or replaced in the future. */
    public int clickShortcut = 0;

    private HandlerRegistration focusHandlerRegistration;
    private HandlerRegistration blurHandlerRegistration;
    private long lastClickTime = 0;

    public VButton() {
        super(DOM.createDiv());
        setTabIndex(0);
        sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.FOCUSEVENTS
                | Event.KEYEVENTS);

        // Add a11y role "button"
        Roles.getButtonRole().set(getElement());

        getElement().appendChild(wrapper);
        wrapper.appendChild(captionElement);

        setStyleName(CLASSNAME);

        addClickHandler(this);
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        wrapper.setClassName(getStylePrimaryName() + "-wrap");
        captionElement.setClassName(getStylePrimaryName() + "-caption");
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        wrapper.setClassName(getStylePrimaryName() + "-wrap");
        captionElement.setClassName(getStylePrimaryName() + "-caption");
    }

    public void setText(String text) {
        captionElement.setInnerText(text);
    }

    public void setHtml(String html) {
        captionElement.setInnerHTML(html);
    }

    @SuppressWarnings("deprecation")
    @Override
    /*
     * Copy-pasted from GWT CustomButton, some minor modifications done:
     * 
     * -for IE/Opera added CLASSNAME_PRESSED
     * 
     * -event.preventDefault() removed from ONMOUSEDOWN (Firefox won't apply
     * :active styles if it is present)
     * 
     * -Tooltip event handling added
     * 
     * -onload event handler added (for icon handling)
     */
    public void onBrowserEvent(Event event) {
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
            // fix for #14632 - on mobile safari 8, if we press the button long
            // enough, we might get two click events, so we are suppressing
            // second if it is too soon
            boolean isPhantomClickPossible = BrowserInfo.get().isSafari()
                    && BrowserInfo.get().isTouchDevice()
                    && BrowserInfo.get().getBrowserMajorVersion() == 8;
            long clickTime = isPhantomClickPossible ? System
                    .currentTimeMillis() : 0;
            // If clicks are currently disallowed or phantom, keep it from
            // bubbling or being passed to the superclass.
            if (disallowNextClick || isPhantomClickPossible
                    && (clickTime - lastClickTime < 100)) { // the maximum
                                                            // interval observed
                                                            // for phantom click
                                                            // is 69, with
                                                            // majority under
                                                            // 50, so we select
                                                            // 100 to be safe
                event.stopPropagation();
                disallowNextClick = false;
                return;
            }
            lastClickTime = clickTime;
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
                addStyleName(CLASSNAME_PRESSED);

                if (BrowserInfo.get().isIE8() || BrowserInfo.get().isIE9()) {
                    /*
                     * We need to prevent the default behavior on these browsers
                     * since user-select is not available.
                     */
                    event.preventDefault();
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

                removeStyleName(CLASSNAME_PRESSED);

                // Explicitly prevent IE 8 from propagating mouseup events
                // upward (fixes #6753)
                if (BrowserInfo.get().isIE8()) {
                    event.stopPropagation();
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
        case Event.ONMOUSEOVER:
            if (isCapturing && isTargetInsideButton(event)) {
                // This means a mousedown happened on the button and a mouseup
                // has not happened yet
                setHovering(true);
                addStyleName(CLASSNAME_PRESSED);
            }
            break;
        case Event.ONMOUSEOUT:
            if (isTargetInsideButton(event)) {
                if (clickPending
                        && Math.abs(mousedownX - event.getClientX()) < MOVE_THRESHOLD
                        && Math.abs(mousedownY - event.getClientY()) < MOVE_THRESHOLD) {
                    onClick();
                    break;
                }
                clickPending = false;
                setHovering(false);
                removeStyleName(CLASSNAME_PRESSED);
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
                // Stop propagation when the user starts pressing a button that
                // we are handling to prevent actions from getting triggered
                if (event.getKeyCode() == 32 /* space */) {
                    isFocusing = true;
                    event.preventDefault();
                    event.stopPropagation();
                } else if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
                    event.stopPropagation();
                }
                break;
            case Event.ONKEYUP:
                if (isFocusing && event.getKeyCode() == 32 /* space */) {
                    isFocusing = false;
                    onClick();
                    event.stopPropagation();
                    event.preventDefault();
                }
                break;
            case Event.ONKEYPRESS:
                if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
                    onClick();
                    event.stopPropagation();
                    event.preventDefault();
                }
                break;
            }
        }
    }

    /**
     * Check if the event occurred over an element which is part of this button
     */
    private boolean isTargetInsideButton(Event event) {
        Element to = event.getRelatedTarget();
        return getElement().isOrHasChild(DOM.eventGetTarget(event))
                && (to == null || !getElement().isOrHasChild(to));
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
    @Override
    public void onClick(ClickEvent event) {
        if (BrowserInfo.get().isSafari()) {
            VButton.this.setFocus(true);
        }

        clickPending = false;
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
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public void onClick() {
        // Allow the click we're about to synthesize to pass through to the
        // superclass and containing elements. Element.dispatchEvent() is
        // synchronous, so we simply set and clear the flag within this method.

        disallowNextClick = false;

        // Screen coordinates are not always available (e.g., when the click is
        // caused by a keyboard event).
        // Set (x,y) client coordinates to the middle of the button
        int x = getElement().getAbsoluteLeft() - getElement().getScrollLeft()
                - getElement().getOwnerDocument().getScrollLeft()
                + WidgetUtil.getRequiredWidth(getElement()) / 2;
        int y = getElement().getAbsoluteTop() - getElement().getScrollTop()
                - getElement().getOwnerDocument().getScrollTop()
                + WidgetUtil.getRequiredHeight(getElement()) / 2;
        NativeEvent evt = Document.get().createClickEvent(1, 0, 0, x, y, false,
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
                Roles.getButtonRole().setAriaDisabledState(getElement(),
                        !enabled);
                super.setTabIndex(-1);
            } else {
                Roles.getButtonRole().removeAriaDisabledState(getElement());
                super.setTabIndex(tabIndex);
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

}
