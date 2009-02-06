/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
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

    /*
     * The z-index value from where all overlays live. This can be overridden in
     * any extending class.
     */
    protected static int Z_INDEX = 20000;

    /*
     * Shadow element style. If an extending class wishes to use a different
     * style of shadow, it can use setShadowStyle(String) to give the shadow
     * element a new style name.
     */
    public static final String CLASSNAME_SHADOW = "i-shadow";

    /*
     * The shadow element for this overlay.
     */
    private Element shadow;

    /**
     * The HTML snippet that is used to render the actual shadow. In consists of
     * nine different DIV-elements with the following class names:
     * 
     * <pre>
     *   .i-shadow[-stylename]
     *   ----------------------------------------------
     *   | .top-left     |   .top    |     .top-right |
     *   |---------------|-----------|----------------|
     *   |               |           |                |
     *   | .left         |  .center  |         .right |
     *   |               |           |                |
     *   |---------------|-----------|----------------|
     *   | .bottom-left  |  .bottom  |  .bottom-right |
     *   ----------------------------------------------
     * </pre>
     * 
     * See default theme 'shadow.css' for implementation example.
     */
    private static final String SHADOW_HTML = "<div class=\"top-left\"></div><div class=\"top\"></div><div class=\"top-right\"></div><div class=\"left\"></div><div class=\"center\"></div><div class=\"right\"></div><div class=\"bottom-left\"></div><div class=\"bottom\"></div><div class=\"bottom-right\"></div>";

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
            shadow = DOM.createDiv();
            shadow.setClassName(CLASSNAME_SHADOW);
            shadow.setInnerHTML(SHADOW_HTML);
            DOM.setStyleAttribute(shadow, "position", "absolute");

            addPopupListener(new PopupListener() {
                public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
                    DOM.removeChild(RootPanel.get().getElement(), shadow);
                }
            });
        }
        adjustZIndex();
    }

    private void adjustZIndex() {
        setZIndex(Z_INDEX);
    }

    /**
     * Set the z-index (visual stack position) for this overlay.
     * 
     * @param zIndex
     *            The new z-index
     */
    protected void setZIndex(int zIndex) {
        DOM.setStyleAttribute(getElement(), "zIndex", "" + zIndex);
        if (shadow != null) {
            DOM.setStyleAttribute(shadow, "zIndex", "" + zIndex);
        }
        if (BrowserInfo.get().isIE6()) {
            adjustIE6Frame(getElement(), zIndex - 1);
        }
    }

    /**
     * Get the z-index (visual stack position) of this overlay.
     * 
     * @return The z-index for this overlay.
     */
    private int getZIndex() {
        return Integer.parseInt(DOM.getStyleAttribute(getElement(), "zIndex"));
    }

    @Override
    public void setPopupPosition(int left, int top) {
        super.setPopupPosition(left, top);
        if (shadow != null) {
            updateShadowSizeAndPosition(isAnimationEnabled() ? 0 : 1);
        }
    }

    @Override
    public void show() {
        super.show();
        if (shadow != null) {
            if (isAnimationEnabled()) {
                ShadowAnimation sa = new ShadowAnimation();
                sa.run(200);
            } else {
                updateShadowSizeAndPosition(1.0);
            }
        }
        if (BrowserInfo.get().isIE6()) {
            adjustIE6Frame(getElement(), getZIndex());
        }
    }

    /*
     * Needed to position overlays on top of native SELECT elements in IE6. See
     * bug #2004
     */
    private native void adjustIE6Frame(Element popup, int zindex)
    /*-{
        // relies on PopupImplIE6
        if(popup.__frame) 
            popup.__frame.style.zIndex = zindex;
    }-*/;

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        if (shadow != null) {
            updateShadowSizeAndPosition(1.0);
        }
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        if (shadow != null) {
            updateShadowSizeAndPosition(1.0);
        }
    }

    /**
     * Sets the shadow style for this overlay. Will override any previous style
     * for the shadow. The default style name is defined by CLASSNAME_SHADOW.
     * The given style will be prefixed with CLASSNAME_SHADOW.
     * 
     * @param style
     *            The new style name for the shadow element. Will be prefixed by
     *            CLASSNAME_SHADOW, e.g. style=='foobar' -> actual style
     *            name=='i-shadow-foobar'.
     */
    protected void setShadowStyle(String style) {
        if (shadow != null) {
            shadow.setClassName(CLASSNAME_SHADOW + "-" + style);
        }
    }

    /*
     * Extending classes should always call this method after they change the
     * size of overlay without using normal 'setWidth(String)' and
     * 'setHeight(String)' methods.
     */
    protected void updateShadowSizeAndPosition() {
        updateShadowSizeAndPosition(1.0);
    }

    /**
     * Recalculates proper position and dimensions for the shadow element. Can
     * be used to animate the shadow, using the 'progress' parameter (used to
     * animate the shadow in sync with GWT PopupPanel's default animation
     * 'PopupPanel.AnimationType.CENTER').
     * 
     * @param progress
     *            A value between 0.0 and 1.0, indicating the progress of the
     *            animation (0=start, 1=end).
     */
    private void updateShadowSizeAndPosition(double progress) {
        // Don't do anything if overlay element is not attached
        if (!isAttached() || !isVisible()) {
            return;
        }
        // Calculate proper z-index
        String zIndex = null;
        try {
            // Odd behaviour with Windows Hosted Mode forces us to use this
            // redundant try/catch block (See dev.itmill.com #2011)
            zIndex = DOM.getStyleAttribute(getElement(), "zIndex");
        } catch (Exception ignore) {
            // Ignored, will cause no harm, other than a little eye-candy
            // missing
        }
        if (zIndex == null) {
            zIndex = "" + Z_INDEX;
        }
        // Calculate position and size
        if (BrowserInfo.get().isIE()) {
            // Shake IE
            getOffsetHeight();
            getOffsetWidth();
        }

        int x = getAbsoluteLeft();
        int y = getAbsoluteTop();

        /* This is needed for IE7 at least */
        // Account for the difference between absolute position and the
        // body's positioning context.
        x -= Document.get().getBodyOffsetLeft();
        y -= Document.get().getBodyOffsetTop();

        int width = getOffsetWidth();
        int height = getOffsetHeight();

        if (width < 0) {
            width = 0;
        }
        if (height < 0) {
            height = 0;
        }

        // Animate the shadow size
        x += (int) (width * (1.0 - progress) / 2.0);
        y += (int) (height * (1.0 - progress) / 2.0);
        width = (int) (width * progress);
        height = (int) (height * progress);

        // Update correct values
        DOM.setStyleAttribute(shadow, "display", progress < 0.9 ? "none" : "");
        DOM.setStyleAttribute(shadow, "zIndex", zIndex);
        DOM.setStyleAttribute(shadow, "width", width + "px");
        DOM.setStyleAttribute(shadow, "height", height + "px");
        DOM.setStyleAttribute(shadow, "top", y + "px");
        DOM.setStyleAttribute(shadow, "left", x + "px");

        // attach to dom if not there already
        if (shadow.getParentElement() == null) {
            RootPanel.get().getElement().insertBefore(shadow, getElement());
        }

    }

    protected class ShadowAnimation extends Animation {
        @Override
        protected void onUpdate(double progress) {
            if (shadow != null) {
                updateShadowSizeAndPosition(progress);
            }
        }
    }
}
