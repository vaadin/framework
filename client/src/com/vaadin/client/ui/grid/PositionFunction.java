/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.client.ui.grid;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;

/**
 * A functional interface that can be used for positioning elements in the DOM.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
interface PositionFunction {
    /**
     * A position function using "transform: translate3d(x,y,z)" to position
     * elements in the DOM.
     */
    public static class Translate3DPosition implements PositionFunction {
        @Override
        public void set(Element e, double x, double y) {
            e.getStyle().setProperty("transform",
                    "translate3d(" + x + "px, " + y + "px, 0)");
        }
    }

    /**
     * A position function using "transform: translate(x,y)" to position
     * elements in the DOM.
     */
    public static class TranslatePosition implements PositionFunction {
        @Override
        public void set(Element e, double x, double y) {
            e.getStyle().setProperty("transform",
                    "translate(" + x + "px," + y + "px)");
        }
    }

    /**
     * A position function using "-webkit-transform: translate3d(x,y,z)" to
     * position elements in the DOM.
     */
    public static class WebkitTranslate3DPosition implements PositionFunction {
        @Override
        public void set(Element e, double x, double y) {
            e.getStyle().setProperty("webkitTransform",
                    "translate3d(" + x + "px," + y + "px,0");
        }
    }

    /**
     * A position function using "left: x" and "top: y" to position elements in
     * the DOM.
     */
    public static class AbsolutePosition implements PositionFunction {
        @Override
        public void set(Element e, double x, double y) {
            e.getStyle().setLeft(x, Unit.PX);
            e.getStyle().setTop(y, Unit.PX);
        }
    }

    /**
     * Position an element in an (x,y) coordinate system in the DOM.
     * 
     * @param e
     *            the element to position. Never <code>null</code>.
     * @param x
     *            the x coordinate, in pixels
     * @param y
     *            the y coordinate, in pixels
     */
    void set(Element e, double x, double y);
}