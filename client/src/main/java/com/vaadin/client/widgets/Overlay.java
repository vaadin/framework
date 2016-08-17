/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.AnimationUtil;
import com.vaadin.client.AnimationUtil.AnimationEndListener;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComputedStyle;

/**
 * Overlay widget extending the PopupPanel. Overlay is used to float elements on
 * top of other elements temporarily.
 * <p>
 * <b>Note:</b> This class should always be constructed with
 * {@link GWT#create(Class)}.
 *
 * @since 7.6.1
 */
public class Overlay extends PopupPanel {

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
     * Matches {@link PopupPanel}.ANIMATION_DURATION
     */
    private static final int POPUP_PANEL_ANIMATION_DURATION = 200;

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

    protected boolean isShimElementEnabled() {
        return shimElement != null;
    }

    private void removeShimElement() {
        if (shimElement != null) {
            shimElement.removeFromParent();
        }
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

    private JavaScriptObject animateInListener;

    private boolean fitInWindow = false;

    private boolean maybeShowWithAnimation() {
        boolean isAttached = isAttached() && isShowing();
        super.show();

        // Don't animate if already visible
        if (isAttached) {
            return false;
        } else {
            // Check if animations are used
            setVisible(false);
            addStyleDependentName(ADDITIONAL_CLASSNAME_ANIMATE_IN);

            ComputedStyle cs = new ComputedStyle(getElement());
            String animationName = AnimationUtil.getAnimationName(cs);
            if (animationName == null) {
                animationName = "";
            }
            setVisible(true);

            if (animationName.contains(ADDITIONAL_CLASSNAME_ANIMATE_IN)) {
                // Disable GWT PopupPanel animation if used
                setAnimationEnabled(false);
                animateInListener = AnimationUtil.addAnimationEndListener(
                        getElement(), new AnimationEndListener() {
                            @Override
                            public void onAnimationEnd(NativeEvent event) {
                                String animationName = AnimationUtil
                                        .getAnimationName(event);
                                if (animationName.contains(
                                        ADDITIONAL_CLASSNAME_ANIMATE_IN)) {
                                    AnimationUtil.removeAnimationEndListener(
                                            getElement(), animateInListener);
                                    removeStyleDependentName(
                                            ADDITIONAL_CLASSNAME_ANIMATE_IN);
                                }
                            }
                        });
                return true;
            } else {
                removeStyleDependentName(ADDITIONAL_CLASSNAME_ANIMATE_IN);
                return false;
            }
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        // Always ensure shadow is removed when the overlay is removed.
        removeShimElement();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
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

        if (needsShimElement()) {

            PositionAndSize positionAndSize = new PositionAndSize(
                    getActualLeft(), getActualTop(), getOffsetWidth(),
                    getOffsetHeight());

            // Animate the size
            positionAndSize.setAnimationFromCenterProgress(progress);

            Element container = getElement().getParentElement();

            if (needsShimElement()) {
                updateShimPosition(positionAndSize);
                if (shimElement.getParentElement() == null) {
                    container.insertBefore(shimElement, getElement());
                }
            }
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
        if (animateIn
                && getStyleName().contains(ADDITIONAL_CLASSNAME_ANIMATE_IN)) {
            AnimationUtil.addAnimationEndListener(getElement(),
                    new AnimationEndListener() {
                        @Override
                        public void onAnimationEnd(NativeEvent event) {
                            if (AnimationUtil.getAnimationName(event).contains(
                                    ADDITIONAL_CLASSNAME_ANIMATE_IN)) {
                                reallyHide(autoClosed);
                            }
                        }
                    });
        } else {
            // Check if animations are used
            addStyleDependentName(ADDITIONAL_CLASSNAME_ANIMATE_OUT);
            ComputedStyle cs = new ComputedStyle(getElement());
            String animationName = AnimationUtil.getAnimationName(cs);
            if (animationName == null) {
                animationName = "";
            }

            if (animateOut && animationName
                    .contains(ADDITIONAL_CLASSNAME_ANIMATE_OUT)) {
                // Disable GWT PopupPanel closing animation if used
                setAnimationEnabled(false);

                AnimationUtil.addAnimationEndListener(getElement(),
                        new AnimationEndListener() {
                            @Override
                            public void onAnimationEnd(NativeEvent event) {
                                String animationName = AnimationUtil
                                        .getAnimationName(event);
                                if (animationName.contains(
                                        ADDITIONAL_CLASSNAME_ANIMATE_OUT)) {
                                    AnimationUtil
                                            .removeAllAnimationEndListeners(
                                                    getElement());
                                    // Remove both animation styles just in
                                    // case
                                    removeStyleDependentName(
                                            ADDITIONAL_CLASSNAME_ANIMATE_IN);
                                    removeStyleDependentName(
                                            ADDITIONAL_CLASSNAME_ANIMATE_OUT);
                                    reallyHide(autoClosed);
                                }
                            }
                        });
                // No event previews should happen after the animation has
                // started
                Overlay.this.setPreviewingAllNativeEvents(false);
            } else {
                removeStyleDependentName(ADDITIONAL_CLASSNAME_ANIMATE_OUT);
                reallyHide(autoClosed);
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
