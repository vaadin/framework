/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.HashMap;

import com.google.gwt.core.client.JsArrayString;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;

class ComponentDetail {

    private TooltipInfo tooltipInfo = new TooltipInfo();

    public ComponentDetail() {

    }

    /**
     * Returns a TooltipInfo assosiated with Component. If element is given,
     * returns an additional TooltipInfo.
     * 
     * @param key
     * @return the tooltipInfo
     */
    public TooltipInfo getTooltipInfo(Object key) {
        if (key == null) {
            return tooltipInfo;
        } else {
            if (additionalTooltips != null) {
                return additionalTooltips.get(key);
            } else {
                return null;
            }
        }
    }

    /**
     * @param tooltipInfo
     *            the tooltipInfo to set
     */
    public void setTooltipInfo(TooltipInfo tooltipInfo) {
        this.tooltipInfo = tooltipInfo;
    }

    private Size offsetSize;
    private HashMap<Object, TooltipInfo> additionalTooltips;

    /**

    /**
     * @return the offsetSize
     */
    Size getOffsetSize() {
        return offsetSize;
    }

    /**
     * @param offsetSize
     *            the offsetSize to set
     */
    void setOffsetSize(Size offsetSize) {
        this.offsetSize = offsetSize;
    }

    public void putAdditionalTooltip(Object key, TooltipInfo tooltip) {
        if (tooltip == null && additionalTooltips != null) {
            additionalTooltips.remove(key);
        } else {
            if (additionalTooltips == null) {
                additionalTooltips = new HashMap<Object, TooltipInfo>();
            }
            additionalTooltips.put(key, tooltip);
        }
    }

    private JsArrayString eventListeners;

    /**
     * Stores the event listeners registered on server-side and passed along in
     * the UIDL.
     * 
     * @param componentUIDL
     *            The UIDL for the component
     * @since 6.2
     */
    native void registerEventListenersFromUIDL(UIDL uidl)
    /*-{
        this.@com.vaadin.terminal.gwt.client.ComponentDetail::eventListeners = uidl[1].eventListeners;
    }-*/;

    /**
     * Checks if there is a registered server side listener for the event.
     * 
     * @param eventIdentifier
     *            The identifier for the event
     * @return true if at least one listener has been registered on server side
     *         for the event identified by eventIdentifier.
     */
    public boolean hasEventListeners(String eventIdentifier) {
        if (eventListeners != null) {
            int l = eventListeners.length();
            for (int i = 0; i < l; i++) {
                if (eventListeners.get(i).equals(eventIdentifier)) {
                    return true;
                }
            }
        }
        return false;
    }
}
