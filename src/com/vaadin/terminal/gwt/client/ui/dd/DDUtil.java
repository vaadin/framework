/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.vaadin.terminal.gwt.client.Util;

public class DDUtil {

    /**
     * @deprecated use the version with the actual event instead of detected
     *             clientY value
     * 
     * @param element
     * @param clientY
     * @param topBottomRatio
     * @return
     */
    @Deprecated
    public static VerticalDropLocation getVerticalDropLocation(Element element,
            int clientY, double topBottomRatio) {
        int offsetHeight = element.getOffsetHeight();
        return getVerticalDropLocation(element, offsetHeight, clientY,
                topBottomRatio);
    }

    public static VerticalDropLocation getVerticalDropLocation(Element element,
            NativeEvent event, double topBottomRatio) {
        int offsetHeight = element.getOffsetHeight();
        return getVerticalDropLocation(element, offsetHeight, event,
                topBottomRatio);
    }

    public static VerticalDropLocation getVerticalDropLocation(Element element,
            int offsetHeight, NativeEvent event, double topBottomRatio) {
        int clientY = Util.getTouchOrMouseClientY(event);
        return getVerticalDropLocation(element, offsetHeight, clientY,
                topBottomRatio);
    }

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

    public static HorizontalDropLocation getHorizontalDropLocation(
            Element element, NativeEvent event, double leftRightRatio) {
        int touchOrMouseClientX = Util.getTouchOrMouseClientX(event);
        return getHorizontalDropLocation(element, touchOrMouseClientX,
                leftRightRatio);
    }

    /**
     * @deprecated use the version with the actual event
     * @param element
     * @param clientX
     * @param leftRightRatio
     * @return
     */
    @Deprecated
    public static HorizontalDropLocation getHorizontalDropLocation(
            Element element, int clientX, double leftRightRatio) {

        // Event coordinates are relative to the viewport, element absolute
        // position is relative to the document. Make element position relative
        // to viewport by adjusting for viewport scrolling. See #6021
        int elementLeft = element.getAbsoluteLeft() - Window.getScrollLeft();
        int offsetWidth = element.getOffsetWidth();
        int fromLeft = clientX - elementLeft;

        float percentageFromTop = (fromLeft / (float) offsetWidth);
        if (percentageFromTop < leftRightRatio) {
            return HorizontalDropLocation.LEFT;
        } else if (percentageFromTop > 1 - leftRightRatio) {
            return HorizontalDropLocation.RIGHT;
        } else {
            return HorizontalDropLocation.CENTER;
        }
    }

}
