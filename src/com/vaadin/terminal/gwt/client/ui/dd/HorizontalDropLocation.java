package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.user.client.Element;

public enum HorizontalDropLocation {
    LEFT, RIGHT, CENTER;

    public static HorizontalDropLocation get(Element element, int clientX,
            double leftRightRatio) {

        int absoluteLeft = element.getAbsoluteLeft();
        int offsetWidth = element.getOffsetWidth();
        int fromTop = clientX - absoluteLeft;

        float percentageFromTop = (fromTop / (float) offsetWidth);
        if (percentageFromTop < leftRightRatio) {
            return LEFT;
        } else if (percentageFromTop > 1 - leftRightRatio) {
            return RIGHT;
        } else {
            return CENTER;
        }
    }
}
