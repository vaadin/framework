package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.user.client.Element;

public enum VerticalDropLocation {
    Top, Bottom, Center;

    public static VerticalDropLocation get(Element element, int clientY,
            double topBottomRatio) {

        int absoluteTop = element.getAbsoluteTop();
        int offsetHeight = element.getOffsetHeight();
        int fromTop = clientY - absoluteTop;

        float percentageFromTop = (fromTop / (float) offsetHeight);
        if (percentageFromTop < topBottomRatio) {
            return Top;
        } else if (percentageFromTop > 1 - topBottomRatio) {
            return Bottom;
        } else {
            return Center;
        }
    }
}
