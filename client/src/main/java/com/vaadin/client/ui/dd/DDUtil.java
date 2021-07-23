/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.client.ui.dd;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Window;
import com.vaadin.client.WidgetUtil;
import com.vaadin.shared.ui.dd.HorizontalDropLocation;
import com.vaadin.shared.ui.dd.VerticalDropLocation;

/**
 * Utility class for drag and drop handling.
 *
 * @author Vaadin Ltd
 */
public class DDUtil {

    /** Utility classes shouldn't be instantiated. */
    private DDUtil() {
    }

    /**
     * Get vertical drop location.
     *
     * @param element
     *            the drop target element
     * @param event
     *            the latest {@link NativeEvent} that relates to this drag
     *            operation
     * @param topBottomRatio
     *            the ratio that determines how big portion of the element on
     *            each end counts for indicating desire to drop above or below
     *            the element rather than on top of it
     * @return the drop location
     */
    public static VerticalDropLocation getVerticalDropLocation(Element element,
            NativeEvent event, double topBottomRatio) {
        int offsetHeight = element.getOffsetHeight();
        return getVerticalDropLocation(element, offsetHeight, event,
                topBottomRatio);
    }

    /**
     * Get vertical drop location.
     *
     * @param element
     *            the drop target element
     * @param offsetHeight
     *            the height of an element relative to the layout
     * @param event
     *            the latest {@link NativeEvent} that relates to this drag
     *            operation
     * @param topBottomRatio
     *            the ratio that determines how big portion of the element on
     *            each end counts for indicating desire to drop above or below
     *            the element rather than on top of it
     * @return the drop location
     */
    public static VerticalDropLocation getVerticalDropLocation(Element element,
            int offsetHeight, NativeEvent event, double topBottomRatio) {
        int clientY = WidgetUtil.getTouchOrMouseClientY(event);
        return getVerticalDropLocation(element, offsetHeight, clientY,
                topBottomRatio);
    }

    /**
     * Get vertical drop location.
     *
     * @param element
     *            the drop target element
     * @param offsetHeight
     *            the height of an element relative to the layout
     * @param clientY
     *            the y-coordinate of the latest event that relates to this drag
     *            operation
     * @param topBottomRatio
     *            the ratio that determines how big portion of the element on
     *            each end counts for indicating desire to drop above or below
     *            the element rather than on top of it
     * @return the drop location
     */
    public static VerticalDropLocation getVerticalDropLocation(Element element,
            int offsetHeight, int clientY, double topBottomRatio) {

        // Event coordinates are relative to the viewport, element absolute
        // position is relative to the document. Make element position relative
        // to viewport by adjusting for viewport scrolling. See #6021
        int elementTop = element.getAbsoluteTop() - Window.getScrollTop();
        int fromTop = clientY - elementTop;

        float percentageFromTop = (fromTop / (float) offsetHeight);
        if (percentageFromTop < topBottomRatio) {
            return VerticalDropLocation.TOP;
        } else if (percentageFromTop > 1 - topBottomRatio) {
            return VerticalDropLocation.BOTTOM;
        } else {
            return VerticalDropLocation.MIDDLE;
        }
    }

    /**
     * Get horizontal drop location.
     *
     * @param element
     *            the drop target element
     * @param event
     *            the latest {@link NativeEvent} that relates to this drag
     *            operation
     * @param leftRightRatio
     *            the ratio that determines how big portion of the element on
     *            each end counts for indicating desire to drop beside the
     *            element rather than on top of it
     * @return the drop location
     */
    public static HorizontalDropLocation getHorizontalDropLocation(
            Element element, NativeEvent event, double leftRightRatio) {
        int clientX = WidgetUtil.getTouchOrMouseClientX(event);

        // Event coordinates are relative to the viewport, element absolute
        // position is relative to the document. Make element position relative
        // to viewport by adjusting for viewport scrolling. See #6021
        int elementLeft = element.getAbsoluteLeft() - Window.getScrollLeft();
        int offsetWidth = element.getOffsetWidth();
        int fromLeft = clientX - elementLeft;

        float percentageFromLeft = (fromLeft / (float) offsetWidth);
        if (percentageFromLeft < leftRightRatio) {
            return HorizontalDropLocation.LEFT;
        } else if (percentageFromLeft > 1 - leftRightRatio) {
            return HorizontalDropLocation.RIGHT;
        } else {
            return HorizontalDropLocation.CENTER;
        }
    }

}
