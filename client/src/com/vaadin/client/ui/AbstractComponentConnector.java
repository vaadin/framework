/*
 * Copyright 2011 Vaadin Ltd.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ComponentContainerConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.LayoutManager;
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
import com.vaadin.client.ui.datefield.PopupDateFieldConnector;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.ComponentState;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.TabIndexState;

public abstract class AbstractComponentConnector extends AbstractConnector
        implements ComponentConnector {

    private Widget widget;

    private String lastKnownWidth = "";
    private String lastKnownHeight = "";

    /**
     * The style names from getState().getStyles() which are currently applied
     * to the widget.
     */
    protected List<String> styleNames = new ArrayList<String>();

    /**
     * Default constructor
     */
    public AbstractComponentConnector() {
    }

    @Override
    protected void init() {
        super.init();

        getConnection().getVTooltip().connectHandlersToWidget(getWidget());

    }

    /**
     * Creates and returns the widget for this VPaintableWidget. This method
     * should only be called once when initializing the paintable.
     * 
     * @return
     */
    protected Widget createWidget() {
        Type type = TypeData.getType(getClass());
        try {
            Type widgetType = type.getMethod("getWidget").getReturnType();
            Object instance = widgetType.createInstance();
            return (Widget) instance;
        } catch (NoDataException e) {
            throw new IllegalStateException(
                    "There is no information about the widget for "
                            + Util.getSimpleName(this)
                            + ". Did you remember to compile the right widgetset?",
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
            widget = createWidget();
        }

        return widget;
    }

    @Deprecated
    public static boolean isRealUpdate(UIDL uidl) {
        return !uidl.hasAttribute("cached");
    }

    @Override
    public ComponentState getState() {
        return (ComponentState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        ConnectorMap paintableMap = ConnectorMap.get(getConnection());

        if (getState().id != null) {
            getWidget().getElement().setId(getState().id);
        } else {
            getWidget().getElement().removeAttribute("id");

        }

        /*
         * Disabled state may affect (override) tabindex so the order must be
         * first setting tabindex, then enabled state (through super
         * implementation).
         */
        if (getState() instanceof TabIndexState
                && getWidget() instanceof Focusable) {
            ((Focusable) getWidget())
                    .setTabIndex(((TabIndexState) getState()).tabIndex);
        }

        super.onStateChanged(stateChangeEvent);

        // Style names
        updateWidgetStyleNames();

        // Set captions
        if (delegateCaptionHandling()) {
            ServerConnector parent = getParent();
            if (parent instanceof ComponentContainerConnector) {
                ((ComponentContainerConnector) parent).updateCaption(this);
            } else if (parent == null && !(this instanceof UIConnector)) {
                VConsole.error("Parent of connector "
                        + Util.getConnectorString(this)
                        + " is null. This is typically an indication of a broken component hierarchy");
            }
        }

        /*
         * updateComponentSize need to be after caption update so caption can be
         * taken into account
         */

        updateComponentSize();
    }

    @Override
    public void setWidgetEnabled(boolean widgetEnabled) {
        // add or remove v-disabled style name from the widget
        setWidgetStyleName(ApplicationConnection.DISABLED_CLASSNAME,
                !widgetEnabled);

        if (getWidget() instanceof HasEnabled) {
            // set widget specific enabled state
            ((HasEnabled) getWidget()).setEnabled(widgetEnabled);

            // make sure the caption has or has not v-disabled style
            if (delegateCaptionHandling()) {
                ServerConnector parent = getParent();
                if (parent instanceof ComponentContainerConnector) {
                    ((ComponentContainerConnector) parent).updateCaption(this);
                } else if (parent == null && !(this instanceof UIConnector)) {
                    VConsole.error("Parent of connector "
                            + Util.getConnectorString(this)
                            + " is null. This is typically an indication of a broken component hierarchy");
                }
            }
        }
    }

    private void updateComponentSize() {
        String newWidth = getState().width == null ? "" : getState().width;
        String newHeight = getState().height == null ? "" : getState().height;

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

        widget.setStyleName("v-has-width", !isUndefinedWidth());
        widget.setStyleName("v-has-height", !isUndefinedHeight());

        widget.setHeight(newHeight);
        widget.setWidth(newWidth);
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
        ComponentState state = getState();

        String primaryStyleName = getWidget().getStylePrimaryName();
        if (state.primaryStyleName != null) {
            /*
             * We overwrite the widgets primary stylename if state defines a
             * primary stylename.
             */
            getWidget().setStylePrimaryName(state.primaryStyleName);
        }

        // Set the core 'v' style name for the widget
        setWidgetStyleName(StyleConstants.UI_WIDGET, true);

        // should be in AbstractFieldConnector ?
        // add / remove read-only style name
        setWidgetStyleName("v-readonly", isReadOnly());

        // add / remove error style name
        setWidgetStyleNameWithPrefix(primaryStyleName,
                ApplicationConnection.ERROR_CLASSNAME_EXT,
                null != state.errorMessage);

        // add additional user defined style names as class names, prefixed with
        // component default class name. remove nonexistent style names.
        if (ComponentStateUtil.hasStyles(state)) {

            // Remove all old stylenames
            for (String oldStyle : styleNames) {
                setWidgetStyleName(oldStyle, false);
                setWidgetStyleNameWithPrefix(primaryStyleName + "-", oldStyle,
                        false);
            }

            // add new style names
            for (String newStyle : state.styles) {
                setWidgetStyleName(newStyle, true);
                setWidgetStyleNameWithPrefix(primaryStyleName + "-", newStyle,
                        true);
            }

            styleNames.clear();
            styleNames.addAll(state.styles);

        } else {
            // remove all old style names
            for (String oldStyle : styleNames) {
                setWidgetStyleName(oldStyle, false);
                setWidgetStyleNameWithPrefix(primaryStyleName + "-", oldStyle,
                        false);
            }
            styleNames.clear();
        }

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
     * {@link PopupDateFieldConnector#setWidgetStyleNameWithPrefix(String, String, boolean)}
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

    /**
     * Checks if there is a registered server side listener for the given event
     * identifier.
     * 
     * @param eventIdentifier
     *            The identifier to check for
     * @return true if an event listener has been registered with the given
     *         event identifier on the server side, false otherwise
     */
    @Override
    public boolean hasEventListener(String eventIdentifier) {
        Set<String> reg = getState().registeredEventListeners;
        return (reg != null && reg.contains(eventIdentifier));
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

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ComponentConnector#getTooltipInfo(com.
     * google.gwt.dom.client.Element)
     */
    @Override
    public TooltipInfo getTooltipInfo(Element element) {
        return new TooltipInfo(getState().description, getState().errorMessage);
    }

    /**
     * Gets the icon set for this component.
     * 
     * @return the URL of the icon, or <code>null</code> if no icon has been
     *         defined.
     */
    protected String getIcon() {
        return getResourceUrl(ComponentConstants.ICON_RESOURCE);
    }
}
