/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.AnimationUtil;
import com.vaadin.client.AnimationUtil.AnimationEndListener;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.WidgetUtil;

/**
 * Overlay widget extending the PopupPanel. Overlay is used to float elements on
 * top of other elements temporarily.
 * <p>
 * <b>Note:</b> This class should always be constructed with
 * {@link GWT#create(Class)}.
 * 
 * <h3>Shadow</h3>
 * <p>
 * The separate shadow element underneath the main overlay element is <strong>
 * <em>deprecated</em></strong>, and should not be used for new overlay
 * components. CSS box-shadow should be used instead of a separate shadow
 * element. Remember to include any vendor-prefixed versions to support all
 * browsers that you need to. To cover all possible browsers that Vaadin 7
 * supports, add <code>-webkit-box-shadow</code> and the standard
 * <code>box-shadow</code> properties.
 * </p>
 * 
 * <p>
 * For IE8, which doesn't support CSS box-shadow, you can use the proprietary
 * DropShadow filter. It doesn't provide the exact same features as box-shadow,
 * but it is suitable for graceful degradation. Other options are to use a
 * border or a pseudo-element underneath the overlay which mimics a shadow, or
 * any combination of these.
 * </p>
 * 
 * <p>
 * Read more about the DropShadow filter from <a
 * href="http://msdn.microsoft.com/en-us/library/ms532985(v=vs.85).aspx"
 * >Microsoft Developer Network</a>
 * </p>
 * 
 * @since 7.6.1
 */
public class Overlay extends PopupPanel implements CloseHandler<PopupPanel> {

    @Override
    protected void onAttach() {
        // Move the overlay to the appropriate overlay container
        final Overlay overlay = Overlay.current;
        if (overlay != null) {
            final Element e = overlay.getOverlayContainer();
            e.appendChild(getElement());
        }

        super.onAttach();
    }

    public static class PositionAndSize {
        private int left, top, width, height;

        public PositionAndSize(int left, int top, int width, int height) {
            super();
            setLeft(left);
            setTop(top);
            setWidth(width);
            setHeight(height);
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            if (width < 0) {
                width = 0;
            }

            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            if (height < 0) {
                height = 0;
            }

            this.height = height;
        }

        public void setAnimationFromCenterProgress(double progress) {
            left += (int) (width * (1.0 - progress) / 2.0);
            top += (int) (height * (1.0 - progress) / 2.0);
            width = (int) (width * progress);
            height = (int) (height * progress);
        }
    }

    /*
     * The z-index value from where all overlays live. This can be overridden in
     * any extending class.
     */
    public static int Z_INDEX = 20000;

    private static int leftFix = -1;

    private static int topFix = -1;

    /**
     * Shadow element style. If an extending class wishes to use a different
     * style of shadow, it can use setShadowStyle(String) to give the shadow
     * element a new style name.
     * 
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    public static final String CLASSNAME_SHADOW = "v-shadow";

    /**
     * Style name for the overlay container element (see
     * {@link #getOverlayContainer()}
     */
    public static final String CLASSNAME_CONTAINER = "v-overlay-container";

    /**
     * @since 7.3
     */
    public static final String ADDITIONAL_CLASSNAME_ANIMATE_IN = "animate-in";
    /**
     * @since 7.3
     */
    public static final String ADDITIONAL_CLASSNAME_ANIMATE_OUT = "animate-out";

    /**
     * The shadow element for this overlay.
     * 
     * @deprecated See main JavaDoc for Overlay
     * 
     */
    @Deprecated
    private Element shadow;

    /*
     * The creator of this Overlay (the widget that made the instance, not the
     * layout parent)
     */
    private Widget owner;

    /**
     * The shim iframe behind the overlay, allowing PDFs and applets to be
     * covered by overlays.
     */
    private IFrameElement shimElement;

    /**
     * The HTML snippet that is used to render the actual shadow. In consists of
     * nine different DIV-elements with the following class names:
     * 
     * <pre>
     *   .v-shadow[-stylename]
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
     * 
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    private static final String SHADOW_HTML = "<div aria-hidden=\"true\" class=\"top-left\"></div><div class=\"top\"></div><div class=\"top-right\"></div><div class=\"left\"></div><div class=\"center\"></div><div class=\"right\"></div><div class=\"bottom-left\"></div><div class=\"bottom\"></div><div class=\"bottom-right\"></div>";

    /**
     * Matches {@link PopupPanel}.ANIMATION_DURATION
     */
    private static final int POPUP_PANEL_ANIMATION_DURATION = 200;

    /**
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    private boolean sinkShadowEvents = false;

    private List<Command> runOnClose = new ArrayList<Command>();

    public Overlay() {
        super();
        adjustZIndex();
    }

    public Overlay(boolean autoHide) {
        super(autoHide);
        adjustZIndex();
    }

    public Overlay(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        adjustZIndex();
    }

    /**
     * @deprecated See main JavaDoc for Overlay. Use the other constructors
     *             without the <code>showShadow</code> parameter.
     */
    @Deprecated
    public Overlay(boolean autoHide, boolean modal, boolean showShadow) {
        super(autoHide, modal);
        setShadowEnabled(showShadow && useShadowDiv());
        adjustZIndex();
    }

    /**
     * Return true if a separate shadow div should be used. Since Vaadin 7.3,
     * shadows are implemented with CSS box-shadow. Thus, a shadow div is only
     * used for IE8 by default.
     * 
     * @deprecated See main JavaDoc for Overlay
     * @since 7.3
     * @return true to use a shadow div
     */
    @Deprecated
    protected boolean useShadowDiv() {
        return BrowserInfo.get().isIE8();
    }

    /**
     * Method to control whether DOM elements for shadow are added. With this
     * method subclasses can control displaying of shadow also after the
     * constructor.
     * 
     * @param enabled
     *            true if shadow should be displayed
     * 
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    protected void setShadowEnabled(boolean enabled) {
        if (enabled != isShadowEnabled()) {
            if (enabled) {
                shadow = DOM.createDiv();
                shadow.setClassName(CLASSNAME_SHADOW);
                shadow.setInnerHTML(SHADOW_HTML);
                shadow.getStyle().setPosition(Position.ABSOLUTE);
                addCloseHandler(this);
            } else {
                removeShadowIfPresent();
                shadow = null;
            }
        }
    }

    /**
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    protected boolean isShadowEnabled() {
        return shadow != null;
    }

    protected boolean isShimElementEnabled() {
        return shimElement != null;
    }

    private void removeShimElement() {
        if (shimElement != null) {
            shimElement.removeFromParent();
        }
    }

    /**
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    private void removeShadowIfPresent() {
        if (isShadowAttached()) {
            // Remove event listener from the shadow
            unsinkShadowEvents();

            shadow.removeFromParent();
        }
    }

    /**
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    private boolean isShadowAttached() {
        return isShadowEnabled() && shadow.getParentElement() != null;
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
        getElement().getStyle().setZIndex(zIndex);
        if (isShadowEnabled()) {
            shadow.getStyle().setZIndex(zIndex);
        }
    }

    @Override
    public void setPopupPosition(int left, int top) {

        // PopupPanel tries to position the popup on screen (by
        // default right, below) and will move it if there is not
        // enough space right or below but only if there is
        // sufficient space left or above. If the popup is too big
        // to fit on either side, it will be in the original
        // position.

        if (isFitInWindow()) {
            int windowLeft = Window.getScrollLeft();
            int windowRight = Window.getScrollLeft() + Window.getClientWidth();
            int width = getOffsetWidth();
            int popupRight = left + width;
            int popupRightOfWindow = popupRight - windowRight;
            if (popupRightOfWindow > 0) {
                // Popup is too large to fit
                left -= popupRightOfWindow;
                if (left < 0) {
                    // Would move left of screen, shrink to fit in window
                    setOuterWidthThroughWidget(windowRight - windowLeft);
                    runOnClose.add(new Command() {
                        @Override
                        public void execute() {
                            getWidget().setWidth("");
                        }
                    });
                    left = 0;
                }
            }

            int windowTop = Window.getScrollTop();
            int windowBottom = Window.getScrollTop() + Window.getClientHeight();
            int height = getOffsetHeight();
            int popupBottom = top + height;
            int popupBelowWindow = popupBottom - windowBottom;
            if (popupBelowWindow > 0) {
                // Popup is too large to fit
                top -= popupBelowWindow;
                if (top < 0) {
                    // Would move above screen, shrink to fit in window
                    setOuterHeightThroughWidget(windowBottom - windowTop);
                    runOnClose.add(new Command() {
                        @Override
                        public void execute() {
                            getWidget().setHeight("");
                        }
                    });
                    top = 0;
                }
            }
        }

        // TODO, this should in fact be part of
        // Document.get().getBodyOffsetLeft/Top(). Would require overriding DOM
        // for all permutations. Now adding fix as margin instead of fixing
        // left/top because parent class saves the position.
        Style style = getElement().getStyle();
        style.setMarginLeft(-adjustByRelativeLeftBodyMargin(), Unit.PX);
        style.setMarginTop(-adjustByRelativeTopBodyMargin(), Unit.PX);
        super.setPopupPosition(left, top);
        positionOrSizeUpdated(isAnimationEnabled() ? 0 : 1);
    }

    private void setOuterHeightThroughWidget(int outerHeight) {
        getWidget().setHeight(outerHeight + "px");

        // Take margin/border/padding into account if needed
        // (the height is for the overlay root but we set it on the
        // widget)
        int adjustedHeight = outerHeight - (getOffsetHeight() - outerHeight);
        if (adjustedHeight != outerHeight) {
            getWidget().setHeight(adjustedHeight + "px");
        }
    }

    private void setOuterWidthThroughWidget(int outerWidth) {
        getWidget().setWidth(outerWidth + "px");

        // Take margin/border/padding into account if needed
        // (the height is for the overlay root but we set it on the
        // widget)
        int adjustedWidth = outerWidth - (getOffsetWidth() - outerWidth);
        if (adjustedWidth != outerWidth) {
            getWidget().setWidth(adjustedWidth + "px");
        }
    }

    private IFrameElement getShimElement() {
        if (shimElement == null && needsShimElement()) {
            shimElement = Document.get().createIFrameElement();

            // Insert shim iframe before the main overlay element. It does not
            // matter if it is in front or behind the shadow as we cannot put a
            // shim behind the shadow due to its transparency.
            shimElement.getStyle().setPosition(Position.ABSOLUTE);
            shimElement.getStyle().setBorderStyle(BorderStyle.NONE);
            shimElement.setTabIndex(-1);
            shimElement.setFrameBorder(0);
            shimElement.setMarginHeight(0);
        }
        return shimElement;
    }

    private int getActualTop() {
        int y = getAbsoluteTop();

        /* This is needed for IE7 at least */
        // Account for the difference between absolute position and the
        // body's positioning context.
        y -= Document.get().getBodyOffsetTop();
        y -= adjustByRelativeTopBodyMargin();

        return y;
    }

    private int getActualLeft() {
        int x = getAbsoluteLeft();

        /* This is needed for IE7 at least */
        // Account for the difference between absolute position and the
        // body's positioning context.
        x -= Document.get().getBodyOffsetLeft();
        x -= adjustByRelativeLeftBodyMargin();

        return x;
    }

    private static int adjustByRelativeTopBodyMargin() {
        if (topFix == -1) {
            topFix = detectRelativeBodyFixes("top");
        }
        return topFix;
    }

    private native static int detectRelativeBodyFixes(String axis)
    /*-{
        try {
            var b = $wnd.document.body;
            var cstyle = b.currentStyle ? b.currentStyle : getComputedStyle(b);
            if(cstyle && cstyle.position == 'relative') {
                return b.getBoundingClientRect()[axis];
            }
        } catch(e){}
        return 0;
    }-*/;

    private static int adjustByRelativeLeftBodyMargin() {
        if (leftFix == -1) {
            leftFix = detectRelativeBodyFixes("left");

        }
        return leftFix;
    }

    /*
     * A "thread local" of sorts, set temporarily so that OverlayImpl knows
     * which Overlay is using it, so that it can be attached to the correct
     * overlay container.
     * 
     * TODO this is a strange pattern that we should get rid of when possible.
     */
    protected static Overlay current;

    @Override
    public void show() {
        current = this;

        maybeShowWithAnimation();

        if (isAnimationEnabled()) {
            new ResizeAnimation().run(POPUP_PANEL_ANIMATION_DURATION);
        } else {
            positionOrSizeUpdated(1.0);
        }
        current = null;
    }

    private boolean fitInWindow = false;

    private boolean maybeShowWithAnimation() {
        boolean isAttached = isAttached() && isShowing();
        super.show();

        // Don't animate if already visible or browser is IE8 or IE9 (no CSS
        // animation support)
        if (isAttached || BrowserInfo.get().isIE8()
                || BrowserInfo.get().isIE9()) {
            return false;
        } else {
            // Check if animations are used
            setVisible(false);
            addStyleDependentName(ADDITIONAL_CLASSNAME_ANIMATE_IN);
            if (isShadowEnabled()) {
                shadow.addClassName(CLASSNAME_SHADOW + "-"
                        + ADDITIONAL_CLASSNAME_ANIMATE_IN);
            }

            ComputedStyle cs = new ComputedStyle(getElement());
            String animationName = AnimationUtil.getAnimationName(cs);
            if (animationName == null) {
                animationName = "";
            }
            setVisible(true);

            if (animationName.contains(ADDITIONAL_CLASSNAME_ANIMATE_IN)) {
                // Disable GWT PopupPanel animation if used
                setAnimationEnabled(false);
                AnimationUtil.addAnimationEndListener(
                        getElement(), new AnimationEndListener() {
                            @Override
                            public void onAnimationEnd(NativeEvent event) {
                                String animationName = AnimationUtil
                                        .getAnimationName(event);
                                if (animationName
                                        .contains(ADDITIONAL_CLASSNAME_ANIMATE_IN)) {
                                    boolean removed = AnimationUtil.removeAnimationEndListener(getElement(), this);
                                    assert removed: "Animation end listener was not removed";
                                    removeStyleDependentName(ADDITIONAL_CLASSNAME_ANIMATE_IN);
                                    if (isShadowEnabled()) {
                                        shadow.removeClassName(CLASSNAME_SHADOW
                                                + "-"
                                                + ADDITIONAL_CLASSNAME_ANIMATE_IN);
                                    }
                                }
                            }
                        });
                return true;
            } else {
                removeStyleDependentName(ADDITIONAL_CLASSNAME_ANIMATE_IN);
                if (isShadowEnabled()) {
                    shadow.removeClassName(CLASSNAME_SHADOW + "-"
                            + ADDITIONAL_CLASSNAME_ANIMATE_IN);
                }
                return false;
            }
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        // Always ensure shadow is removed when the overlay is removed.
        removeShadowIfPresent();
        removeShimElement();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (isShadowEnabled()) {
            shadow.getStyle().setProperty("visibility",
                    visible ? "visible" : "hidden");
        }
        if (isShimElementEnabled()) {
            shimElement.getStyle().setProperty("visibility",
                    visible ? "visible" : "hidden");
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        positionOrSizeUpdated(1.0);
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        positionOrSizeUpdated(1.0);
    }

    /**
     * Sets the shadow style for this overlay. Will override any previous style
     * for the shadow. The default style name is defined by CLASSNAME_SHADOW.
     * The given style will be prefixed with CLASSNAME_SHADOW.
     * 
     * @param style
     *            The new style name for the shadow element. Will be prefixed by
     *            CLASSNAME_SHADOW, e.g. style=='foobar' -> actual style
     *            name=='v-shadow-foobar'.
     * 
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    protected void setShadowStyle(String style) {
        if (isShadowEnabled()) {
            shadow.setClassName(CLASSNAME_SHADOW + "-" + style);
        }
    }

    /**
     * Extending classes should always call this method after they change the
     * size of overlay without using normal 'setWidth(String)' and
     * 'setHeight(String)' methods (if not calling super.setWidth/Height).
     * 
     */
    public void positionOrSizeUpdated() {
        positionOrSizeUpdated(1.0);
    }

    /**
     * @deprecated Call {@link #positionOrSizeUpdated()} instead.
     */
    @Deprecated
    protected void updateShadowSizeAndPosition() {
        positionOrSizeUpdated();
    }

    /**
     * Recalculates proper position and dimensions for the shadow and shim
     * elements. Can be used to animate the related elements, using the
     * 'progress' parameter (used to animate the shadow in sync with GWT
     * PopupPanel's default animation 'PopupPanel.AnimationType.CENTER').
     * 
     * @param progress
     *            A value between 0.0 and 1.0, indicating the progress of the
     *            animation (0=start, 1=end).
     */
    private void positionOrSizeUpdated(final double progress) {
        // Don't do anything if overlay element is not attached
        if (!isAttached()) {
            return;
        }
        // Calculate proper z-index
        int zIndex = -1;
        try {
            // Odd behaviour with Windows Hosted Mode forces us to use
            // this redundant try/catch block (See dev.vaadin.com #2011)
            zIndex = Integer.parseInt(getElement().getStyle().getZIndex());
        } catch (Exception ignore) {
            // Ignored, will cause no harm
            zIndex = 1000;
        }
        if (zIndex == -1) {
            zIndex = Z_INDEX;
        }
        // Calculate position and size
        if (BrowserInfo.get().isIE()) {
            // Shake IE
            getOffsetHeight();
            getOffsetWidth();
        }

        if (isShadowEnabled() || needsShimElement()) {

            PositionAndSize positionAndSize = new PositionAndSize(
                    getActualLeft(), getActualTop(), getOffsetWidth(),
                    getOffsetHeight());

            // Animate the size
            positionAndSize.setAnimationFromCenterProgress(progress);

            Element container = getElement().getParentElement();

            if (isShadowEnabled()) {
                updateShadowPosition(progress, zIndex, positionAndSize);
                if (shadow.getParentElement() == null) {
                    container.insertBefore(shadow, getElement());
                    sinkShadowEvents();
                }
            }

            if (needsShimElement()) {
                updateShimPosition(positionAndSize);
                if (shimElement.getParentElement() == null) {
                    container.insertBefore(shimElement, getElement());
                }
            }
        }
        // Fix for #14173
        // IE9 and IE10 have a bug, when resize an a element with box-shadow.
        // IE9 and IE10 need explicit update to remove extra box-shadows
        if (BrowserInfo.get().isIE9() || BrowserInfo.get().isIE10()) {
            WidgetUtil.forceIERedraw(getElement());
        }
    }

    /**
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    private void updateShadowPosition(final double progress, int zIndex,
            PositionAndSize positionAndSize) {
        // Opera needs some shaking to get parts of the shadow showing
        // properly (ticket #2704)
        if (BrowserInfo.get().isOpera()) {
            // Clear the height of all middle elements
            DOM.getChild(shadow, 3).getStyle().setProperty("height", "auto");
            DOM.getChild(shadow, 4).getStyle().setProperty("height", "auto");
            DOM.getChild(shadow, 5).getStyle().setProperty("height", "auto");
        }

        updatePositionAndSize(shadow, positionAndSize);
        shadow.getStyle().setZIndex(zIndex);
        shadow.getStyle().setProperty("display", progress < 0.9 ? "none" : "");

        // Opera fix, part 2 (ticket #2704)
        if (BrowserInfo.get().isOpera()) {
            // We'll fix the height of all the middle elements
            DOM.getChild(shadow, 3)
                    .getStyle()
                    .setPropertyPx("height",
                            DOM.getChild(shadow, 3).getOffsetHeight());
            DOM.getChild(shadow, 4)
                    .getStyle()
                    .setPropertyPx("height",
                            DOM.getChild(shadow, 4).getOffsetHeight());
            DOM.getChild(shadow, 5)
                    .getStyle()
                    .setPropertyPx("height",
                            DOM.getChild(shadow, 5).getOffsetHeight());
        }
    }

    private void updateShimPosition(PositionAndSize positionAndSize) {
        updatePositionAndSize(getShimElement(), positionAndSize);
    }

    /**
     * Returns true if we should add a shim iframe below the overlay to deal
     * with zindex issues with PDFs and applets. Can be overriden to disable
     * shim iframes if they are not needed.
     * 
     * @return true if a shim iframe should be added, false otherwise
     */
    protected boolean needsShimElement() {
        BrowserInfo info = BrowserInfo.get();
        return info.isIE() && info.isBrowserVersionNewerOrEqual(8, 0);
    }

    private void updatePositionAndSize(Element e,
            PositionAndSize positionAndSize) {
        e.getStyle().setLeft(positionAndSize.getLeft(), Unit.PX);
        e.getStyle().setTop(positionAndSize.getTop(), Unit.PX);
        e.getStyle().setWidth(positionAndSize.getWidth(), Unit.PX);
        e.getStyle().setHeight(positionAndSize.getHeight(), Unit.PX);
    }

    protected class ResizeAnimation extends Animation {
        @Override
        protected void onUpdate(double progress) {
            positionOrSizeUpdated(progress);
        }
    }

    @Override
    public void onClose(CloseEvent<PopupPanel> event) {
        removeShadowIfPresent();
    }

    @Override
    public void sinkEvents(int eventBitsToAdd) {
        super.sinkEvents(eventBitsToAdd);
        // Also sink events on the shadow if present
        sinkShadowEvents();
    }

    /**
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    private void sinkShadowEvents() {
        if (isSinkShadowEvents() && isShadowAttached()) {
            // Sink the same events as the actual overlay has sunk
            DOM.sinkEvents(shadow, DOM.getEventsSunk(getElement()));
            // Send events to Overlay.onBrowserEvent
            DOM.setEventListener(shadow, this);
        }
    }

    /**
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    private void unsinkShadowEvents() {
        if (isShadowAttached()) {
            DOM.setEventListener(shadow, null);
            DOM.sinkEvents(shadow, 0);
        }
    }

    /**
     * Enables or disables sinking the events of the shadow to the same
     * onBrowserEvent as events to the actual overlay goes.
     * 
     * Please note, that if you enable this, you can't assume that e.g.
     * event.getEventTarget returns an element inside the DOM structure of the
     * overlay
     * 
     * @param sinkShadowEvents
     * 
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    protected void setSinkShadowEvents(boolean sinkShadowEvents) {
        this.sinkShadowEvents = sinkShadowEvents;
        if (sinkShadowEvents) {
            sinkShadowEvents();
        } else {
            unsinkShadowEvents();
        }
    }

    /**
     * @deprecated See main JavaDoc for Overlay
     */
    @Deprecated
    protected boolean isSinkShadowEvents() {
        return sinkShadowEvents;
    }

    /**
     * Get owner (Widget that made this Overlay, not the layout parent) of
     * Overlay
     * 
     * @return Owner (creator) or null if not defined
     */
    public Widget getOwner() {
        return owner;
    }

    /**
     * Set owner (Widget that made this Overlay, not the layout parent) of
     * Overlay
     * 
     * @param owner
     *            Owner (creator) of Overlay
     */
    public void setOwner(Widget owner) {
        this.owner = owner;
    }

    /**
     * Gets the 'overlay container' element.
     * 
     * @return the overlay container element
     */
    public com.google.gwt.user.client.Element getOverlayContainer() {
        return RootPanel.get().getElement();
    }

    @Override
    public void center() {
        super.center();

        // Some devices can be zoomed in, we should center to the visual
        // viewport for those devices
        BrowserInfo b = BrowserInfo.get();
        if (b.isAndroid() || b.isIOS()) {
            int left = (getVisualViewportWidth() - getOffsetWidth()) >> 1;
            int top = (getVisualViewportHeight() - getOffsetHeight()) >> 1;
            setPopupPosition(Math.max(Window.getScrollLeft() + left, 0),
                    Math.max(Window.getScrollTop() + top, 0));
        }

    }

    /**
     * Gets the visual viewport width, which is useful for e.g iOS where the
     * view can be zoomed in while keeping the layout viewport intact.
     * 
     * Falls back to layout viewport; for those browsers/devices the difference
     * is that the scrollbar with is included (if there is a scrollbar).
     * 
     * @since 7.0.7
     * @return
     */
    private int getVisualViewportWidth() {
        int w = (int) getSubpixelInnerWidth();
        if (w < 0) {
            return Window.getClientWidth();
        } else {
            return w;
        }
    }

    /**
     * Gets the visual viewport height, which is useful for e.g iOS where the
     * view can be zoomed in while keeping the layout viewport intact.
     * 
     * Falls back to layout viewport; for those browsers/devices the difference
     * is that the scrollbar with is included (if there is a scrollbar).
     * 
     * @since 7.0.7
     * @return
     */
    private int getVisualViewportHeight() {
        int h = (int) getSubpixelInnerHeight();
        if (h < 0) {
            return Window.getClientHeight();
        } else {
            return h;
        }
    }

    private native double getSubpixelInnerWidth()
    /*-{
         return $wnd.innerWidth !== undefined ? $wnd.innerWidth : -1;
    }-*/;

    private native double getSubpixelInnerHeight()
    /*-{
         return $wnd.innerHeight !== undefined ? $wnd.innerHeight :-1;
    }-*/;

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.PopupPanel#hide()
     */
    @Override
    public void hide() {
        hide(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.PopupPanel#hide(boolean)
     */
    @Override
    public void hide(final boolean autoClosed) {
        hide(autoClosed, true, true);
    }

    /**
     * 
     * Hides the popup and detaches it from the page. This has no effect if it
     * is not currently showing. Animation-in, animation-out can be
     * enable/disabled for different use cases.
     * 
     * @see com.google.gwt.user.client.ui.PopupPanel#hide(boolean)
     * 
     * @param autoClosed
     *            the value that will be passed to
     *            {@link CloseHandler#onClose(CloseEvent)} when the popup is
     *            closed
     * @param animateIn
     *            enable/disable animate-in animation
     * @param animateOut
     *            enable/disable animate-out animation
     * @since 7.3.7
     */
    public void hide(final boolean autoClosed, final boolean animateIn,
            final boolean animateOut) {
        if (BrowserInfo.get().isIE8() || BrowserInfo.get().isIE9()) {
            reallyHide(autoClosed);
        } else {
            if (animateIn
                    && getStyleName().contains(ADDITIONAL_CLASSNAME_ANIMATE_IN)) {
                AnimationUtil.addAnimationEndListener(getElement(),
                        new AnimationEndListener() {
                            @Override
                            public void onAnimationEnd(NativeEvent event) {
                                if (AnimationUtil
                                        .getAnimationName(event)
                                        .contains(
                                                ADDITIONAL_CLASSNAME_ANIMATE_IN)) {
                                    boolean removed = AnimationUtil.removeAnimationEndListener(getElement(), this);
                                    assert removed: "Animation end listener was not removed";
                                    reallyHide(autoClosed);
                                }
                            }
                        });
            } else {
                // Check if animations are used
                addStyleDependentName(ADDITIONAL_CLASSNAME_ANIMATE_OUT);
                if (isShadowEnabled()) {
                    shadow.addClassName(CLASSNAME_SHADOW + "-"
                            + ADDITIONAL_CLASSNAME_ANIMATE_OUT);
                }
                ComputedStyle cs = new ComputedStyle(getElement());
                String animationName = AnimationUtil.getAnimationName(cs);
                if (animationName == null) {
                    animationName = "";
                }

                if (animateOut
                        && animationName
                                .contains(ADDITIONAL_CLASSNAME_ANIMATE_OUT)) {
                    // Disable GWT PopupPanel closing animation if used
                    setAnimationEnabled(false);

                    AnimationUtil.addAnimationEndListener(getElement(),
                            new AnimationEndListener() {
                                @Override
                                public void onAnimationEnd(NativeEvent event) {
                                    String animationName = AnimationUtil
                                            .getAnimationName(event);
                                    if (animationName
                                            .contains(ADDITIONAL_CLASSNAME_ANIMATE_OUT)) {
                                        boolean removed = AnimationUtil.removeAnimationEndListener(getElement(), this);
                                        assert removed: "Animation end listener was not removed";
                                        // Remove both animation styles just in
                                        // case
                                        removeStyleDependentName(ADDITIONAL_CLASSNAME_ANIMATE_IN);
                                        removeStyleDependentName(ADDITIONAL_CLASSNAME_ANIMATE_OUT);
                                        if (isShadowEnabled()) {
                                            shadow.removeClassName(CLASSNAME_SHADOW
                                                    + "-"
                                                    + ADDITIONAL_CLASSNAME_ANIMATE_IN);
                                            shadow.removeClassName(CLASSNAME_SHADOW
                                                    + "-"
                                                    + ADDITIONAL_CLASSNAME_ANIMATE_OUT);
                                        }
                                        reallyHide(autoClosed);
                                    }
                                }
                            });
                    // No event previews should happen after the animation has
                    // started
                    Overlay.this.setPreviewingAllNativeEvents(false);
                } else {
                    removeStyleDependentName(ADDITIONAL_CLASSNAME_ANIMATE_OUT);
                    if (isShadowEnabled()) {
                        shadow.removeClassName(CLASSNAME_SHADOW + "-"
                                + ADDITIONAL_CLASSNAME_ANIMATE_OUT);
                    }
                    reallyHide(autoClosed);
                }
            }
        }
    }

    private void reallyHide(boolean autoClosed) {
        super.hide(autoClosed);
        for (Command c : runOnClose) {
            c.execute();
        }
        runOnClose.clear();
    }

    /**
     * Sets whether the overlay should be moved or shrunk to fit inside the
     * window.
     * <p>
     * When this is <code>false</code>, the default {@link PopupPanel} behavior
     * is used, which tries to position the popup primarly below and to the
     * right of a reference UIObject and, if there is not enough space, above or
     * to the left.
     * <p>
     * When this is <code>true</code>, the popup will be moved up/left in case
     * it does not fit on either side. If the popup is larger than the window,
     * it will be shrunk to fit and assume that scrolling e.g. using
     * <code>overflow:auto</code>, is taken care of by the overlay user.
     * 
     * @since 7.6.6
     * @param fitInWindow
     *            <code>true</code> to ensure that no part of the popup is
     *            outside the visible view, <code>false</code> to use the
     *            default {@link PopupPanel} behavior
     */
    public void setFitInWindow(boolean fitInWindow) {
        this.fitInWindow = fitInWindow;
    }

    /**
     * Checks whether the overlay should be moved or shrunk to fit inside the
     * window.
     * 
     * @see #setFitInWindow(boolean)
     * 
     * @since 7.6.6
     * @return <code>true</code> if the popup will be moved and/or shrunk to fit
     *         inside the window, <code>false</code> otherwise
     */
    public boolean isFitInWindow() {
        return fitInWindow;
    }
}
