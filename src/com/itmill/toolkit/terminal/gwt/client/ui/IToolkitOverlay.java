/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;

/**
 * In Toolkit UI this Overlay should always be used for all elements that
 * temporary float over other components like context menus etc. This is to deal
 * stacking order correctly with IWindow objects.
 */
public class IToolkitOverlay extends PopupPanel {

    public static final int Z_INDEX = 20000;

    private Shadow shadow;

    public IToolkitOverlay() {
        super();
        adjustZIndex();
    }

    public IToolkitOverlay(boolean autoHide) {
        super(autoHide);
        adjustZIndex();
    }

    public IToolkitOverlay(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        adjustZIndex();
    }

    public IToolkitOverlay(boolean autoHide, boolean modal, boolean showShadow) {
        super(autoHide, modal);
        if (showShadow) {
            shadow = new Shadow();
        }
        adjustZIndex();
    }

    private void adjustZIndex() {
        DOM.setStyleAttribute(getElement(), "zIndex", "" + Z_INDEX);
    }

    @Override
    public void setPopupPosition(int left, int top) {
        super.setPopupPosition(left, top);
        if (shadow != null) {
            shadow.updateSizeAndPosition(isAnimationEnabled() ? 0 : 1);
        }
    }

    @Override
    public void show() {
        super.show();
        if (shadow != null) {
            DOM.appendChild(RootPanel.get().getElement(), shadow.getElement());
            if (isAnimationEnabled()) {
                ShadowAnimation sa = new ShadowAnimation();
                sa.run(200);
            } else {
                shadow.updateSizeAndPosition(1.0);
            }
        }
        if (BrowserInfo.get().isIE6()) {
            adjustIE6Frame(getElement(), Z_INDEX - 1);
        }
    }

    private native void adjustIE6Frame(Element popup, int zindex)
    /*-{
        // relies on PopupImplIE6
        popup.__frame.style.zIndex = zindex;
    }-*/;

    public void setShadowOffset(int top, int right, int bottom, int left) {
        if (shadow != null) {
            shadow.setOffset(top, right, bottom, left);
        }
    }

    private class Shadow extends HTML {

        private static final String CLASSNAME = "i-shadow";

        private static final String HTML = "<div class=\"top-left\"></div><div class=\"top\"></div><div class=\"top-right\"></div><div class=\"left\"></div><div class=\"center\"></div><div class=\"right\"></div><div class=\"bottom-left\"></div><div class=\"bottom\"></div><div class=\"bottom-right\"></div>";

        // Amount of shadow on each side.
        private int top = 2;
        private int right = 5;
        private int bottom = 6;
        private int left = 5;

        public Shadow() {
            super(HTML);
            setStyleName(CLASSNAME);
            DOM.setStyleAttribute(getElement(), "position", "absolute");

            addPopupListener(new PopupListener() {
                public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
                    DOM.removeChild(RootPanel.get().getElement(), shadow
                            .getElement());
                }
            });
        }

        public void updateSizeAndPosition(double phase) {
            // Calculate proper z-index
            String zIndex = null;
            if (IToolkitOverlay.this.isAttached()) {
                // Odd behaviour with Windows Hosted Mode forces us to use a
                // redundant try/catch block (See dev.itmill.com #2011)
                try {
                    zIndex = DOM.getStyleAttribute(IToolkitOverlay.this
                            .getElement(), "zIndex");
                } catch (Exception ignore) {
                    // Ignored, will cause no harm, other than a little
                    // eye-candy missing
                }
            }
            if (zIndex == null) {
                zIndex = "" + Z_INDEX;
            }

            // Calculate position and size
            if (BrowserInfo.get().isIE()) {
                // Shake IE
                IToolkitOverlay.this.getOffsetHeight();
                IToolkitOverlay.this.getOffsetWidth();
            }
            int x = IToolkitOverlay.this.getAbsoluteLeft() - left;
            int y = IToolkitOverlay.this.getAbsoluteTop() - top;
            int width = IToolkitOverlay.this.getOffsetWidth() + left + right;
            int height = IToolkitOverlay.this.getOffsetHeight() + top + bottom;
            if (width < 0) {
                width = 0;
            }
            if (height < 0) {
                height = 0;
            }

            // Animate the shadow size
            x += (int) (width * (1.0 - phase) / 2.0);
            y += (int) (height * (1.0 - phase) / 2.0);
            width = (int) (width * phase);
            height = (int) (height * phase);

            // Update correct values
            DOM.setStyleAttribute(getElement(), "display", phase < 0.9 ? "none"
                    : "");
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
            if (IToolkitOverlay.this.isAttached()) {
                updateSizeAndPosition(1.0);
            }
        }

    }

    class ShadowAnimation extends Animation {

        protected void onUpdate(double progress) {
            if (shadow != null) {
                shadow.updateSizeAndPosition(progress);
            }
        }

    }
}
