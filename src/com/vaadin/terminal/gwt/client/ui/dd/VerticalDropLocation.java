package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.user.client.Element;

public enum VerticalDropLocation {
    TOP, BOTTOM, MIDDLE;

    public static VerticalDropLocation get(Element element, int clientY,
            double topBottomRatio) {

        int absoluteTop = element.getAbsoluteTop();
        int offsetHeight = element.getOffsetHeight();
        int fromTop = clientY - absoluteTop;

        float percentageFromTop = (fromTop / (float) offsetHeight);
        if (percentageFromTop < topBottomRatio) {
            return TOP;
        } else if (percentageFromTop > 1 - topBottomRatio) {
            return BOTTOM;
        } else {
            return MIDDLE;
        }
    }
}
