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
package com.vaadin.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.MouseEventDetails.MouseButton;

/**
 * Helper class for constructing a MouseEventDetails object from a
 * {@link NativeEvent}.
 * 
 * @author Vaadin Ltd
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
        mouseEventDetails.setClientX(WidgetUtil.getTouchOrMouseClientX(evt));
        mouseEventDetails.setClientY(WidgetUtil.getTouchOrMouseClientY(evt));
        if (evt.getButton() == NativeEvent.BUTTON_LEFT) {
            mouseEventDetails.setButton(MouseButton.LEFT);
        } else if (evt.getButton() == NativeEvent.BUTTON_RIGHT) {
            mouseEventDetails.setButton(MouseButton.RIGHT);
        } else if (evt.getButton() == NativeEvent.BUTTON_MIDDLE) {
            mouseEventDetails.setButton(MouseButton.MIDDLE);
        } else {
            // IE8 does not always report a button. Assume left.
            mouseEventDetails.setButton(MouseButton.LEFT);
        }
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
