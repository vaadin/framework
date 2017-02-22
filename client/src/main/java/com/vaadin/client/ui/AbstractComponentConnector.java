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
package com.vaadin.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.HasComponentsConnector;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.Profiler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Type;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ContextClickRpc;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.TabIndexState;
import com.vaadin.shared.ui.ui.UIState;

public abstract class AbstractComponentConnector extends AbstractConnector
        implements ComponentConnector, HasErrorIndicator {

    private HandlerRegistration contextHandler = null;

    private Widget widget;

    private String lastKnownWidth = "";
    private String lastKnownHeight = "";

    private boolean tooltipListenersAttached = false;

    /**
     * The style names from getState().getStyles() which are currently applied
     * to the widget.
     */
    private JsArrayString styleNames = JsArrayString.createArray().cast();

    private Timer longTouchTimer;

    // TODO encapsulate into a nested class
    private HandlerRegistration touchStartHandler;
    private HandlerRegistration touchMoveHandler;
    private HandlerRegistration touchEndHandler;
    private int touchStartX;
    private int touchStartY;
    private boolean preventNextTouchEnd = false;

    protected int SIGNIFICANT_MOVE_THRESHOLD = 20; // pixels

    // long touch event delay
    // TODO replace with global constant for accessibility
    private static final int TOUCH_CONTEXT_MENU_TIMEOUT = 500;

    /**
     * Default constructor
     */
    public AbstractComponentConnector() {
    }

    @OnStateChange("registeredEventListeners")
    void handleContextClickListenerChange() {
        if (contextHandler == null && hasEventListener(EventId.CONTEXT_CLICK)) {
            contextHandler = getWidget()
                    .addDomHandler(new ContextMenuHandler() {
                        @Override
                        public void onContextMenu(ContextMenuEvent event) {
                            final MouseEventDetails mouseEventDetails = MouseEventDetailsBuilder
                                    .buildMouseEventDetails(
                                            event.getNativeEvent(),
                                            getWidget().getElement());

                            event.preventDefault();
                            event.stopPropagation();
                            sendContextClickEvent(mouseEventDetails,
                                    event.getNativeEvent().getEventTarget());
                        }
                    }, ContextMenuEvent.getType());

            // if the widget has a contextclick listener, add touch support as
            // well.

            if (shouldHandleLongTap()) {
                registerTouchHandlers();
            }

        } else if (contextHandler != null
                && !hasEventListener(EventId.CONTEXT_CLICK)) {
            contextHandler.removeHandler();
            contextHandler = null;

            // remove the touch handlers as well
            unregisterTouchHandlers();
        }
    }

    /**
     * The new default behaviour is for long taps to fire a contextclick event
     * if there's a contextclick listener attached to the component.
     *
     * If you do not want this in your component, override this with a blank
     * method to get rid of said behaviour.
     *
     * @since 7.6
     */
    protected void unregisterTouchHandlers() {
        if (touchStartHandler != null) {
            touchStartHandler.removeHandler();
            touchStartHandler = null;
        }
        if (touchMoveHandler != null) {
            touchMoveHandler.removeHandler();
            touchMoveHandler = null;
        }
        if (touchEndHandler != null) {
            touchEndHandler.removeHandler();
            touchEndHandler = null;
        }
    }

    /**
     * The new default behaviour is for long taps to fire a contextclick event
     * if there's a contextclick listener attached to the component.
     *
     * If you do not want this in your component, override this with a blank
     * method to get rid of said behaviour.
     *
     * Some Vaadin Components already handle the long tap as a context menu.
     * This method is unnecessary for those.
     *
     * @since 7.6
     */
    protected void registerTouchHandlers() {
        touchStartHandler = getWidget().addDomHandler(new TouchStartHandler() {

            @Override
            public void onTouchStart(final TouchStartEvent event) {
                if (longTouchTimer != null && longTouchTimer.isRunning()) {
                    return;
                }

                // Prevent selection for the element while pending long tap.
                WidgetUtil.setTextSelectionEnabled(getWidget().getElement(),
                        false);

                if (BrowserInfo.get().isAndroid()) {
                    // Android fires ContextMenu events automatically.
                    return;
                }

                /*
                 * we need to build mouseEventDetails eagerly - the event won't
                 * be guaranteed to be around when the timer executes. At least
                 * this was the case with iOS devices.
                 */

                final MouseEventDetails mouseEventDetails = MouseEventDetailsBuilder
                        .buildMouseEventDetails(event.getNativeEvent(),
                                getWidget().getElement());

                final EventTarget eventTarget = event.getNativeEvent()
                        .getEventTarget();

                longTouchTimer = new Timer() {

                    @Override
                    public void run() {
                        // we're handling this event, our parent components
                        // don't need to bother with it anymore.
                        cancelParentTouchTimers();
                        // The default context click
                        // implementation only provides the
                        // mouse coordinates relative to root
                        // element of widget.

                        sendContextClickEvent(mouseEventDetails, eventTarget);
                        preventNextTouchEnd = true;
                    }
                };

                Touch touch = event.getChangedTouches().get(0);
                touchStartX = touch.getClientX();
                touchStartY = touch.getClientY();

                longTouchTimer.schedule(TOUCH_CONTEXT_MENU_TIMEOUT);

            }
        }, TouchStartEvent.getType());

        touchMoveHandler = getWidget().addDomHandler(new TouchMoveHandler() {

            @Override
            public void onTouchMove(TouchMoveEvent event) {
                if (isSignificantMove(event)) {
                    // Moved finger before the context menu timer
                    // expired, so let the browser handle the event.
                    cancelTouchTimer();
                }

            }

            // mostly copy-pasted code from VScrollTable
            // TODO refactor main logic to a common class
            private boolean isSignificantMove(TouchMoveEvent event) {
                if (longTouchTimer == null) {
                    // no touch start
                    return false;
                }

                // Calculate the distance between touch start and the current
                // touch
                // position
                Touch touch = event.getChangedTouches().get(0);
                int deltaX = touch.getClientX() - touchStartX;
                int deltaY = touch.getClientY() - touchStartY;
                int delta = deltaX * deltaX + deltaY * deltaY;

                // Compare to the square of the significant move threshold to
                // remove the need for a square root
                if (delta > SIGNIFICANT_MOVE_THRESHOLD
                        * SIGNIFICANT_MOVE_THRESHOLD) {
                    return true;
                }
                return false;
            }
        }, TouchMoveEvent.getType());

        touchEndHandler = getWidget().addDomHandler(new TouchEndHandler() {

            @Override
            public void onTouchEnd(TouchEndEvent event) {
                // cancel the timer so the event doesn't fire
                cancelTouchTimer();

                if (preventNextTouchEnd) {
                    event.preventDefault();
                    preventNextTouchEnd = false;
                }
            }
        }, TouchEndEvent.getType());
    }

    protected boolean shouldHandleLongTap() {
        return BrowserInfo.get().isTouchDevice();
    }

    /**
     * If a long touch event timer is running, cancel it.
     *
     * @since 7.6
     */
    private void cancelTouchTimer() {
        WidgetUtil.setTextSelectionEnabled(getWidget().getElement(), true);
        if (longTouchTimer != null) {
            // Re-enable text selection
            longTouchTimer.cancel();
        }
    }

    /**
     * Cancel the timer recursively for parent components that have timers
     * running
     *
     * @since 7.6
     */
    private void cancelParentTouchTimers() {
        ServerConnector parent = getParent();

        // we have to account for the parent being something other than an
        // abstractcomponent. getParent returns null for the root element.

        while (parent != null) {
            if (parent instanceof AbstractComponentConnector) {
                ((AbstractComponentConnector) parent).cancelTouchTimer();
            }
            parent = parent.getParent();
        }

    }

    /**
     * This method sends the context menu event to the server-side. Can be
     * overridden to provide extra information through an alternative RPC
     * interface.
     *
     * @since 7.6
     * @param event
     */
    protected void sendContextClickEvent(MouseEventDetails details,
            EventTarget eventTarget) {

        // The default context click implementation only provides the mouse
        // coordinates relative to root element of widget.
        getRpcProxy(ContextClickRpc.class).contextClick(details);

        WidgetUtil.clearTextSelection();
    }

    /**
     * Creates and returns the widget for this VPaintableWidget. This method
     * should only be called once when initializing the paintable.
     * <p>
     * You should typically not override this method since the framework by
     * default generates an implementation that uses {@link GWT#create(Class)}
     * to create a widget of the same type as returned by the most specific
     * override of {@link #getWidget()}. If you do override the method, you
     * can't call <code>super.createWidget()</code> since the metadata needed
     * for that implementation is not generated if there's an override of the
     * method.
     *
     * @return a new widget instance to use for this component connector
     */
    protected Widget createWidget() {
        Type type = TypeData.getType(getClass());
        try {
            Type widgetType = type.getMethod("getWidget").getReturnType();
            Object instance = widgetType.createInstance();
            return (Widget) instance;
        } catch (NoDataException e) {
            throw new IllegalStateException(
                    "Default implementation of createWidget() does not work for "
                            + getClass().getSimpleName()
                            + ". This might be caused by explicitely using "
                            + "super.createWidget() or some unspecified "
                            + "problem with the widgetset compilation.",
                    e);
        }
    }

    /**
     * Returns the widget associated with this paintable. The widget returned by
     * this method must not changed during the life time of the paintable.
     *
     * @return The widget associated with this paintable
     */
    @Override
    public Widget getWidget() {
        if (widget == null) {
            if (Profiler.isEnabled()) {
                Profiler.enter("AbstractComponentConnector.createWidget for "
                        + getClass().getSimpleName());
            }
            widget = createWidget();
            if (Profiler.isEnabled()) {
                Profiler.leave("AbstractComponentConnector.createWidget for "
                        + getClass().getSimpleName());
            }
        }

        return widget;
    }

    @Deprecated
    public static boolean isRealUpdate(UIDL uidl) {
        return !uidl.hasAttribute("cached");
    }

    @Override
    public AbstractComponentState getState() {
        return (AbstractComponentState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        Profiler.enter("AbstractComponentConnector.onStateChanged");
        Profiler.enter("AbstractComponentConnector.onStateChanged update id");
        if (stateChangeEvent.hasPropertyChanged("id")) {
            if (getState().id != null) {
                getWidget().getElement().setId(getState().id);
            } else if (!stateChangeEvent.isInitialStateChange()) {
                getWidget().getElement().removeAttribute("id");
            }
        }
        Profiler.leave("AbstractComponentConnector.onStateChanged update id");

        /*
         * Disabled state may affect (override) tabindex so the order must be
         * first setting tabindex, then enabled state (through super
         * implementation).
         */
        Profiler.enter(
                "AbstractComponentConnector.onStateChanged update tab index");
        if (getState() instanceof TabIndexState) {
            if (getWidget() instanceof Focusable) {
                ((Focusable) getWidget())
                        .setTabIndex(((TabIndexState) getState()).tabIndex);
            } else {
                /*
                 * TODO Enable this error when all widgets have been fixed to
                 * properly support tabIndex, i.e. implement Focusable
                 */
                // VConsole.error("Tab index received for "
                // + Util.getSimpleName(getWidget())
                // + " which does not implement Focusable");
            }
        } else if (getState() instanceof UIState
                && getWidget() instanceof Focusable) {
            // UI behaves like a component with TabIndexState
            ((Focusable) getWidget())
                    .setTabIndex(((UIState) getState()).tabIndex);
        }
        Profiler.leave(
                "AbstractComponentConnector.onStateChanged update tab index");

        Profiler.enter(
                "AbstractComponentConnector.onStateChanged AbstractConnector.onStateChanged()");
        super.onStateChanged(stateChangeEvent);
        Profiler.leave(
                "AbstractComponentConnector.onStateChanged AbstractConnector.onStateChanged()");

        // Style names
        Profiler.enter(
                "AbstractComponentConnector.onStateChanged updateWidgetStyleNames");
        updateWidgetStyleNames();
        Profiler.leave(
                "AbstractComponentConnector.onStateChanged updateWidgetStyleNames");

        /*
         * updateComponentSize need to be after caption update so caption can be
         * taken into account
         */

        Profiler.enter(
                "AbstractComponentConnector.onStateChanged updateComponentSize");
        updateComponentSize();
        Profiler.leave(
                "AbstractComponentConnector.onStateChanged updateComponentSize");

        Profiler.enter(
                "AbstractComponentContainer.onStateChanged check tooltip");
        if (!tooltipListenersAttached && hasTooltip()) {
            /*
             * Add event handlers for tooltips if they are needed but have not
             * yet been added.
             */
            tooltipListenersAttached = true;
            getConnection().getVTooltip().connectHandlersToWidget(getWidget());
        }
        Profiler.leave(
                "AbstractComponentContainer.onStateChanged check tooltip");

        Profiler.leave("AbstractComponentConnector.onStateChanged");
    }

    @Override
    public void setWidgetEnabled(boolean widgetEnabled) {
        // add or remove v-disabled style name from the widget
        setWidgetStyleName(StyleConstants.DISABLED, !widgetEnabled);

        if (getWidget() instanceof HasEnabled) {
            // set widget specific enabled state
            ((HasEnabled) getWidget()).setEnabled(widgetEnabled);
        }

        // make sure the caption has or has not v-disabled style
        if (delegateCaptionHandling()) {
            ServerConnector parent = getParent();
            if (parent instanceof HasComponentsConnector) {
                ((HasComponentsConnector) parent).updateCaption(this);
            } else if (parent == null && !(this instanceof UIConnector)) {
                VConsole.error("Parent of connector "
                        + Util.getConnectorString(this)
                        + " is null. This is typically an indication of a broken component hierarchy");
            }
        }

    }

    /**
     * Updates the component size based on the shared state, invoking the
     * {@link LayoutManager layout manager} if necessary.
     */
    protected void updateComponentSize() {
        updateComponentSize(getState().width == null ? "" : getState().width,
                getState().height == null ? "" : getState().height);
    }

    /**
     * Updates the component size, invoking the {@link LayoutManager layout
     * manager} if necessary.
     *
     * @param newWidth
     *            The new width as a CSS string. Cannot be null.
     * @param newHeight
     *            The new height as a CSS string. Cannot be null.
     */
    protected void updateComponentSize(String newWidth, String newHeight) {
        Profiler.enter("AbstractComponentConnector.updateComponentSize");

        // Parent should be updated if either dimension changed between relative
        // and non-relative
        if (newWidth.endsWith("%") != lastKnownWidth.endsWith("%")) {
            Connector parent = getParent();
            if (parent instanceof ManagedLayout) {
                getLayoutManager()
                        .setNeedsHorizontalLayout((ManagedLayout) parent);
            }
        }

        if (newHeight.endsWith("%") != lastKnownHeight.endsWith("%")) {
            Connector parent = getParent();
            if (parent instanceof ManagedLayout) {
                getLayoutManager()
                        .setNeedsVerticalLayout((ManagedLayout) parent);
            }
        }

        lastKnownWidth = newWidth;
        lastKnownHeight = newHeight;

        // Set defined sizes
        Widget widget = getWidget();

        Profiler.enter(
                "AbstractComponentConnector.updateComponentSize update styleNames");
        widget.setStyleName("v-has-width", !isUndefinedWidth());
        widget.setStyleName("v-has-height", !isUndefinedHeight());
        Profiler.leave(
                "AbstractComponentConnector.updateComponentSize update styleNames");

        Profiler.enter(
                "AbstractComponentConnector.updateComponentSize update DOM");
        updateWidgetSize(newWidth, newHeight);
        Profiler.leave(
                "AbstractComponentConnector.updateComponentSize update DOM");

        Profiler.leave("AbstractComponentConnector.updateComponentSize");
    }

    /**
     * Updates the DOM size of this connector's {@link #getWidget() widget}.
     *
     * @since 7.1.15
     * @param newWidth
     *            The new width as a CSS string. Cannot be null.
     * @param newHeight
     *            The new height as a CSS string. Cannot be null.
     */
    protected void updateWidgetSize(String newWidth, String newHeight) {
        getWidget().setWidth(newWidth);
        getWidget().setHeight(newHeight);
    }

    @Override
    public boolean isRelativeHeight() {
        return ComponentStateUtil.isRelativeHeight(getState());
    }

    @Override
    public boolean isRelativeWidth() {
        return ComponentStateUtil.isRelativeWidth(getState());
    }

    @Override
    public boolean isUndefinedHeight() {
        return ComponentStateUtil.isUndefinedHeight(getState());
    }

    @Override
    public boolean isUndefinedWidth() {
        return ComponentStateUtil.isUndefinedWidth(getState());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.ComponentConnector#delegateCaptionHandling ()
     */
    @Override
    public boolean delegateCaptionHandling() {
        return true;
    }

    /**
     * Updates the user defined, read-only and error style names for the widget
     * based the shared state. User defined style names are prefixed with the
     * primary style name of the widget returned by {@link #getWidget()}
     * <p>
     * This method can be overridden to provide additional style names for the
     * component, for example see {@code AbstractFieldConnector}
     * </p>
     */
    protected void updateWidgetStyleNames() {
        Profiler.enter("AbstractComponentConnector.updateWidgetStyleNames");
        AbstractComponentState state = getState();

        String primaryStyleName = getWidget().getStylePrimaryName();

        // Set the core 'v' style name for the widget
        setWidgetStyleName(StyleConstants.UI_WIDGET, true);

        // add / remove error style name
        setWidgetStyleNameWithPrefix(primaryStyleName, StyleConstants.ERROR_EXT,
                null != state.errorMessage);

        // add additional user defined style names as class names, prefixed with
        // component default class name. remove nonexistent style names.

        // Remove all old stylenames
        for (int i = 0; i < styleNames.length(); i++) {
            String oldStyle = styleNames.get(i);
            setWidgetStyleName(oldStyle, false);
            setWidgetStyleNameWithPrefix(primaryStyleName + "-", oldStyle,
                    false);
        }
        styleNames.setLength(0);

        if (ComponentStateUtil.hasStyles(state)) {
            // add new style names
            for (String newStyle : state.styles) {
                setWidgetStyleName(newStyle, true);
                setWidgetStyleNameWithPrefix(primaryStyleName + "-", newStyle,
                        true);
                styleNames.push(newStyle);
            }

        }

        if (state.primaryStyleName != null
                && !state.primaryStyleName.equals(primaryStyleName)) {
            /*
             * We overwrite the widgets primary stylename if state defines a
             * primary stylename. This has to be done after updating other
             * styles to be sure the dependent styles are updated correctly.
             */
            getWidget().setStylePrimaryName(state.primaryStyleName);
        }

        // set required style name if components supports that
        if (this instanceof HasRequiredIndicator) {
            getWidget().setStyleName(StyleConstants.REQUIRED,
                    ((HasRequiredIndicator) this).isRequiredIndicatorVisible());
        }

        Profiler.leave("AbstractComponentConnector.updateWidgetStyleNames");
    }

    /**
     * This is used to add / remove state related style names from the widget.
     * <p>
     * Override this method for example if the style name given here should be
     * updated in another widget in addition to the one returned by the
     * {@link #getWidget()}.
     * </p>
     *
     * @param styleName
     *            the style name to be added or removed
     * @param add
     *            <code>true</code> to add the given style, <code>false</code>
     *            to remove it
     */
    protected void setWidgetStyleName(String styleName, boolean add) {
        getWidget().setStyleName(styleName, add);
    }

    /**
     * This is used to add / remove state related prefixed style names from the
     * widget.
     * <p>
     * Override this method if the prefixed style name given here should be
     * updated in another widget in addition to the one returned by the
     * <code>Connector</code>'s {@link #getWidget()}, or if the prefix should be
     * different. For example see
     * {@link com.vaadin.client.ui.datefield.TextualDateConnector#setWidgetStyleNameWithPrefix(String, String, boolean)}
     * </p>
     *
     * @param styleName
     *            the style name to be added or removed
     * @param add
     *            <code>true</code> to add the given style, <code>false</code>
     *            to remove it
     * @deprecated This will be removed once styles are no longer added with
     *             prefixes.
     */
    @Deprecated
    protected void setWidgetStyleNameWithPrefix(String prefix, String styleName,
            boolean add) {
        if (!styleName.startsWith("-")) {
            if (!prefix.endsWith("-")) {
                prefix += "-";
            }
        } else {
            if (prefix.endsWith("-")) {
                styleName.replaceFirst("-", "");
            }
        }
        getWidget().setStyleName(prefix + styleName, add);
    }

    @Override
    public LayoutManager getLayoutManager() {
        return LayoutManager.get(getConnection());
    }

    @Override
    public void updateEnabledState(boolean enabledState) {
        super.updateEnabledState(enabledState);

        setWidgetEnabled(isEnabled());
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        // Show an error if widget is still attached to DOM. It should never be
        // at this point.
        if (getWidget() != null && getWidget().isAttached()) {
            getWidget().removeFromParent();
            VConsole.error(
                    "Widget is still attached to the DOM after the connector ("
                            + Util.getConnectorString(this)
                            + ") has been unregistered. Widget was removed.");
        }
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {
        return new TooltipInfo(getState().description,
                getState().descriptionContentMode, getState().errorMessage);
    }

    @Override
    public boolean hasTooltip() {
        // Normally, there is a tooltip if description or errorMessage is set
        AbstractComponentState state = getState();
        if (state.description != null && !state.description.equals("")) {
            return true;
        } else if (state.errorMessage != null
                && !state.errorMessage.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the URI of the icon set for this component.
     *
     * @return the URI of the icon, or <code>null</code> if no icon has been
     *         defined.
     */
    protected String getIconUri() {
        return getResourceUrl(ComponentConstants.ICON_RESOURCE);
    }

    /**
     * Gets the icon set for this component.
     *
     * @return the icon, or <code>null</code> if no icon has been defined.
     */
    protected Icon getIcon() {
        return getConnection().getIcon(getIconUri());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.ComponentConnector#flush()
     */
    @Override
    public void flush() {
        // No generic implementation. Override if needed
    }

    @Override
    public boolean isErrorIndicatorVisible() {
        return getState().errorMessage != null;
    }
}
