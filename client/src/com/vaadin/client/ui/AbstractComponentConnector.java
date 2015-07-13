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
package com.vaadin.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.HasComponentsConnector;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.Profiler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Type;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.TabIndexState;

public abstract class AbstractComponentConnector extends AbstractConnector
        implements ComponentConnector {

    private Widget widget;

    private String lastKnownWidth = "";
    private String lastKnownHeight = "";

    private boolean tooltipListenersAttached = false;

    /**
     * The style names from getState().getStyles() which are currently applied
     * to the widget.
     */
    private JsArrayString styleNames = JsArrayString.createArray().cast();

    /**
     * Default constructor
     */
    public AbstractComponentConnector() {
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
                            + "problem with the widgetset compilation.", e);
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
        Profiler.enter("AbstractComponentConnector.onStateChanged update tab index");
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
        }
        Profiler.leave("AbstractComponentConnector.onStateChanged update tab index");

        Profiler.enter("AbstractComponentConnector.onStateChanged AbstractConnector.onStateChanged()");
        super.onStateChanged(stateChangeEvent);
        Profiler.leave("AbstractComponentConnector.onStateChanged AbstractConnector.onStateChanged()");

        // Style names
        Profiler.enter("AbstractComponentConnector.onStateChanged updateWidgetStyleNames");
        updateWidgetStyleNames();
        Profiler.leave("AbstractComponentConnector.onStateChanged updateWidgetStyleNames");

        /*
         * updateComponentSize need to be after caption update so caption can be
         * taken into account
         */

        Profiler.enter("AbstractComponentConnector.onStateChanged updateComponentSize");
        updateComponentSize();
        Profiler.leave("AbstractComponentConnector.onStateChanged updateComponentSize");

        Profiler.enter("AbstractComponentContainer.onStateChanged check tooltip");
        if (!tooltipListenersAttached && hasTooltip()) {
            /*
             * Add event handlers for tooltips if they are needed but have not
             * yet been added.
             */
            tooltipListenersAttached = true;
            getConnection().getVTooltip().connectHandlersToWidget(getWidget());
        }
        Profiler.leave("AbstractComponentContainer.onStateChanged check tooltip");

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
                getLayoutManager().setNeedsHorizontalLayout(
                        (ManagedLayout) parent);
            }
        }

        if (newHeight.endsWith("%") != lastKnownHeight.endsWith("%")) {
            Connector parent = getParent();
            if (parent instanceof ManagedLayout) {
                getLayoutManager().setNeedsVerticalLayout(
                        (ManagedLayout) parent);
            }
        }

        lastKnownWidth = newWidth;
        lastKnownHeight = newHeight;

        // Set defined sizes
        Widget widget = getWidget();

        Profiler.enter("AbstractComponentConnector.updateComponentSize update styleNames");
        widget.setStyleName("v-has-width", !isUndefinedWidth());
        widget.setStyleName("v-has-height", !isUndefinedHeight());
        Profiler.leave("AbstractComponentConnector.updateComponentSize update styleNames");

        Profiler.enter("AbstractComponentConnector.updateComponentSize update DOM");
        updateWidgetSize(newWidth, newHeight);
        Profiler.leave("AbstractComponentConnector.updateComponentSize update DOM");

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
     * component, for example see
     * {@link AbstractFieldConnector#updateWidgetStyleNames()}
     * </p>
     */
    protected void updateWidgetStyleNames() {
        Profiler.enter("AbstractComponentConnector.updateWidgetStyleNames");
        AbstractComponentState state = getState();

        String primaryStyleName = getWidget().getStylePrimaryName();

        // Set the core 'v' style name for the widget
        setWidgetStyleName(StyleConstants.UI_WIDGET, true);

        // should be in AbstractFieldConnector ?
        // add / remove read-only style name
        setWidgetStyleName("v-readonly", isReadOnly());

        // add / remove error style name
        setWidgetStyleNameWithPrefix(primaryStyleName,
                StyleConstants.ERROR_EXT, null != state.errorMessage);

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
     * {@link com.vaadin.client.ui.datefield.DateFieldConnector#setWidgetStyleNameWithPrefix(String, String, boolean)}
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
    protected void setWidgetStyleNameWithPrefix(String prefix,
            String styleName, boolean add) {
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

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ComponentConnector#isReadOnly()
     */
    @Override
    @Deprecated
    public boolean isReadOnly() {
        return getState().readOnly;
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
            VConsole.error("Widget is still attached to the DOM after the connector ("
                    + Util.getConnectorString(this)
                    + ") has been unregistered. Widget was removed.");
        }
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {
        return new TooltipInfo(getState().description, getState().errorMessage);
    }

    @Override
    public boolean hasTooltip() {
        // Normally, there is a tooltip if description or errorMessage is set
        AbstractComponentState state = getState();
        if (state.description != null && !state.description.equals("")) {
            return true;
        } else if (state.errorMessage != null && !state.errorMessage.equals("")) {
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
}
