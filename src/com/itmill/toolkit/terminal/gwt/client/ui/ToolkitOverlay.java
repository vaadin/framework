/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;

/**
 * In Toolkit UI this Overlay should always be used for all elements that
 * temporary float over other components like context menus etc. This is to deal
 * stacking order correctly with IWindow objects.
 */
public class ToolkitOverlay extends PopupPanel {

    public static final int Z_INDEX = 20000;

    private Shadow shadow;

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

    public ToolkitOverlay(boolean autoHide, boolean modal, boolean showShadow) {
        super(autoHide, modal);
        if (showShadow) {
            shadow = new Shadow(this);
        }
        adjustZIndex();
    }

    private void adjustZIndex() {
        DOM.setStyleAttribute(getElement(), "zIndex", "" + Z_INDEX);
    }

    public void setPopupPosition(int left, int top) {
        super.setPopupPosition(left, top);
        if (shadow != null) {
            shadow.updateSizeAndPosition();
        }
    }

    public void show() {
        super.show();
        if (shadow != null) {
            DOM.appendChild(RootPanel.get().getElement(), shadow.getElement());
            shadow.updateSizeAndPosition();
        }
    }
    
    public void setShadowOffset(int top, int right, int bottom, int left) {
        if(shadow != null) {
            shadow.setOffset(top, right, bottom, left);
        }
    }

    private class Shadow extends HTML {

        private static final String CLASSNAME = "i-shadow";

        private static final String HTML = "<div class=\"top-left\"></div><div class=\"top\"></div><div class=\"top-right\"></div><div class=\"left\"></div><div class=\"center\"></div><div class=\"right\"></div><div class=\"bottom-left\"></div><div class=\"bottom\"></div><div class=\"bottom-right\"></div>";

        private Widget overlay;

        // Amount of shadow on each side.
        private int top = 2;
        private int right = 5;
        private int bottom = 6;
        private int left = 5;

        public Shadow(ToolkitOverlay overlay) {
            super(HTML);
            setStyleName(CLASSNAME);
            DOM.setStyleAttribute(getElement(), "position", "absolute");

            this.overlay = overlay;
            overlay.addPopupListener(new PopupListener() {
                public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
                    DOM.removeChild(RootPanel.get().getElement(), shadow.getElement());
                }
            });
        }

        public void updateSizeAndPosition() {
            // Calculate proper z-index
            String zIndex = DOM.getStyleAttribute(overlay.getElement(),
                    "zIndex");
            if (zIndex == null) {
                zIndex = "" + Z_INDEX;
            }

            // Calculate position and size
            if(BrowserInfo.get().isIE()) {
                // Shake IE
                overlay.getOffsetHeight();
                overlay.getOffsetWidth();
            }
            int x = overlay.getAbsoluteLeft() - left;
            int y = overlay.getAbsoluteTop() - top;
            int width = overlay.getOffsetWidth() + left + right;
            int height = overlay.getOffsetHeight() + top + bottom;
            if (width < 0) {
                width = 0;
            }
            if (height < 0) {
                height = 0;
            }

            // Update correct values
            DOM.setStyleAttribute(shadow.getElement(), "zIndex", ""
                    + (Integer.parseInt(zIndex) - 1));
            DOM.setStyleAttribute(getElement(), "width", width + "px");
            DOM.setStyleAttribute(getElement(), "height", height + "px");
            DOM.setStyleAttribute(getElement(), "top", y + "px");
            DOM.setStyleAttribute(getElement(), "left", x + "px");
        }
        
        public void setOffset(int top, int right, int bottom, int left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
            if(overlay.isAttached()) {
                updateSizeAndPosition();
            }
        }

    }

}
