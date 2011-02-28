package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Element;
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

        int absoluteTop = element.getAbsoluteTop();
        int fromTop = clientY - absoluteTop;

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

        int absoluteLeft = element.getAbsoluteLeft();
        int offsetWidth = element.getOffsetWidth();
        int fromTop = clientX - absoluteLeft;

        float percentageFromTop = (fromTop / (float) offsetWidth);
        if (percentageFromTop < leftRightRatio) {
            return HorizontalDropLocation.LEFT;
        } else if (percentageFromTop > 1 - leftRightRatio) {
            return HorizontalDropLocation.RIGHT;
        } else {
            return HorizontalDropLocation.CENTER;
        }
    }

}
