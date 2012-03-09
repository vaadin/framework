/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

/**
 * Helper class for constructing a MouseEventDetails object from a
 * {@link NativeEvent}.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public class MouseEventDetailsBuilder {

    /**
     * Construct a {@link MouseEventDetails} object from the given event
     * 
     * @param evt
     *            The event to use as a source for the details
     * @return a MouseEventDetails containing information from the event
     */
    public static MouseEventDetails buildMouseEventDetails(NativeEvent evt) {
        return buildMouseEventDetails(evt, null);
    }

    /**
     * Construct a {@link MouseEventDetails} object from the given event
     * 
     * @param evt
     *            The event to use as a source for the details
     * @param relativeToElement
     *            The element whose position
     *            {@link MouseEventDetails#getRelativeX()} and
     *            {@link MouseEventDetails#getRelativeY()} are relative to.
     * @return a MouseEventDetails containing information from the event
     */
    public static MouseEventDetails buildMouseEventDetails(NativeEvent evt,
            Element relativeToElement) {
        MouseEventDetails mouseEventDetails = new MouseEventDetails();
        mouseEventDetails.setType(Event.getTypeInt(evt.getType()));
        mouseEventDetails.setClientX(Util.getTouchOrMouseClientX(evt));
        mouseEventDetails.setClientY(Util.getTouchOrMouseClientY(evt));
        mouseEventDetails.setButton(evt.getButton());
        mouseEventDetails.setAltKey(evt.getAltKey());
        mouseEventDetails.setCtrlKey(evt.getCtrlKey());
        mouseEventDetails.setMetaKey(evt.getMetaKey());
        mouseEventDetails.setShiftKey(evt.getShiftKey());
        if (relativeToElement != null) {
            mouseEventDetails.setRelativeX(getRelativeX(
                    mouseEventDetails.getClientX(), relativeToElement));
            mouseEventDetails.setRelativeY(getRelativeY(
                    mouseEventDetails.getClientY(), relativeToElement));
        }
        return mouseEventDetails;

    }

    private static int getRelativeX(int clientX, Element target) {
        return clientX - target.getAbsoluteLeft() + target.getScrollLeft()
                + target.getOwnerDocument().getScrollLeft();
    }

    private static int getRelativeY(int clientY, Element target) {
        return clientY - target.getAbsoluteTop() + target.getScrollTop()
                + target.getOwnerDocument().getScrollTop();
    }

}
