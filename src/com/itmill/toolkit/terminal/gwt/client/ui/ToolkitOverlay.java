package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * In Toolkit UI this Overlay should always be used for all elements that
 * temporary float over other components like context menus etc. This is to deal
 * stacking order correctly with IWindow objects.
 */
public class ToolkitOverlay extends PopupPanel {

    public static final int Z_INDEX = 20000;

    public ToolkitOverlay() {
        super();
        adjustZIndex();
    }

    public ToolkitOverlay(boolean autoHide) {
        super(autoHide);
        adjustZIndex();
    }

    public ToolkitOverlay(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        adjustZIndex();
    }

    private void adjustZIndex() {
        DOM.setStyleAttribute(getElement(), "zIndex", "" + (Z_INDEX));
    }

}
