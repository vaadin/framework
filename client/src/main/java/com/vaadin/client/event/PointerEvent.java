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
package com.vaadin.client.event;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;

/**
 * Abstract class representing Pointer events.
 * 
 * @param <H>
 *            handler type
 * 
 * @since 7.2
 */
public abstract class PointerEvent<H extends EventHandler> extends
        MouseEvent<H> {

    enum EventType {
        PointerDown, PointerMove, PointerOut, PointerOver, PointerUp, PointerCancel;

        String getNativeEventName() {
            return PointerEventSupport.getNativeEventName(this);
        }
    }

    public static final String TYPE_UNKNOWN = "";
    public static final String TYPE_TOUCH = "touch";
    public static final String TYPE_PEN = "pen";
    public static final String TYPE_MOUSE = "mouse";

    /**
     * Gets a unique identifier for the pointer that caused this event. The
     * identifiers of previously active but retired pointers may be recycled.
     * 
     * @return unique pointer id
     */
    public final int getPointerId() {
        return getPointerId(getNativeEvent());
    }

    /**
     * Gets the width of the contact geometry of the pointer in CSS pixels.
     * 
     * @return width in CSS pixels
     */
    public final int getWidth() {
        return getWidth(getNativeEvent());
    }

    /**
     * Gets the height of the contact geometry of the pointer in CSS pixels.
     * 
     * @return height in CSS pixels.
     */
    public final int getHeight() {
        return getHeight(getNativeEvent());
    }

    /**
     * Gets the pressure of the pointer input as a value in the range of [0, 1]
     * where 0 and 1 represent the minimum and maximum, respectively.
     * 
     * @return input pressure as a value between 0 and 1
     */
    public final double getPressure() {
        return getPressure(getNativeEvent());
    }

    /**
     * Gets the angle between the Y-Z plane and the plane containing both the
     * transducer and the Y axis. A positive tilt is to the right.
     * 
     * @return the tilt along the X axis as degrees in the range of [-90, 90],
     *         or 0 if the device does not support tilt
     */
    public final double getTiltX() {
        return getTiltX(getNativeEvent());
    }

    /**
     * Gets the angle between the X-Z plane and the plane containing both the
     * transducer and the X axis. A positive tilt is towards the user.
     * 
     * @return the tilt along the Y axis as degrees in the range of [-90, 90],
     *         or 0 if the device does not support tilt
     */
    public final double getTiltY() {
        return getTiltY(getNativeEvent());
    }

    /**
     * Gets the type of the pointer device that caused this event.
     * 
     * @see PointerEvent#TYPE_UNKNOWN
     * @see PointerEvent#TYPE_TOUCH
     * @see PointerEvent#TYPE_PEN
     * @see PointerEvent#TYPE_MOUSE
     * 
     * @return a String indicating the type of the pointer device
     */
    public final String getPointerType() {
        return getPointerType(getNativeEvent());
    }

    /**
     * Indicates whether the pointer is the primary pointer of this type.
     * 
     * @return true if the pointer is the primary pointer, otherwise false
     */
    public final boolean isPrimary() {
        return isPrimary(getNativeEvent());
    }

    private static native final int getPointerId(NativeEvent e)
    /*-{
      return e.pointerId;
    }-*/;

    private static native final int getWidth(NativeEvent e)
    /*-{
      return e.width;
    }-*/;

    private static native final int getHeight(NativeEvent e)
    /*-{
      return e.height;
    }-*/;

    private static native final double getPressure(NativeEvent e)
    /*-{
      return e.pressure;
    }-*/;

    private static native final double getTiltX(NativeEvent e)
    /*-{
      return e.tiltX;
    }-*/;

    private static native final double getTiltY(NativeEvent e)
    /*-{
      return e.tiltY;
    }-*/;

    private static native final String getPointerType(NativeEvent e)
    /*-{
      var pointerType = e.pointerType;
      if (typeof pointerType === "number") {
          pointerType = [ , , "touch", "pen", "mouse" ][pointerType];
      }
      return pointerType || "";
    }-*/;

    private static native final boolean isPrimary(NativeEvent e)
    /*-{
      return e.isPrimary;
    }-*/;

}
