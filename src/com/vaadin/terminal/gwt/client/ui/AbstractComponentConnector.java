/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.Set;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.LayoutManager;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.TooltipInfo;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.communication.SharedState;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;

public abstract class AbstractComponentConnector extends AbstractConnector
        implements ComponentConnector {

    public interface ClickRPC extends ServerRpc {
        /**
         * Called when a click event has occurred and there are server side
         * listeners for the event.
         * 
         * @param mouseDetails
         *            Details about the mouse when the event took place
         */
        public void click(MouseEventDetails mouseDetails);
    }

    private ComponentContainerConnector parent;

    private Widget widget;

    // shared state from the server to the client
    private ComponentState state;

    private String lastKnownWidth = "";
    private String lastKnownHeight = "";

    /**
     * Default constructor
     */
    public AbstractComponentConnector() {
    }

    /**
     * Creates and returns the widget for this VPaintableWidget. This method
     * should only be called once when initializing the paintable.
     * 
     * @return
     */
    protected abstract Widget createWidget();

    /**
     * Returns the widget associated with this paintable. The widget returned by
     * this method must not changed during the life time of the paintable.
     * 
     * @return The widget associated with this paintable
     */
    public Widget getWidget() {
        if (widget == null) {
            widget = createWidget();
        }

        return widget;
    }

    /**
     * Returns the shared state object for this connector.
     * 
     * If overriding this method to return a more specific type, also
     * {@link #createState()} must be overridden.
     * 
     * @return current shared state (not null)
     */
    public ComponentState getState() {
        return state;
    }

    public static boolean isRealUpdate(UIDL uidl) {
        return !uidl.hasAttribute("cached");
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        ConnectorMap paintableMap = ConnectorMap.get(getConnection());

        if (getState().getDebugId() != null) {
            getWidget().getElement().setId(getState().getDebugId());
        } else {
            getWidget().getElement().setId(null);

        }

        /*
         * Disabled state may affect (override) tabindex so the order must be
         * first setting tabindex, then enabled state.
         */
        if (state instanceof TabIndexState && getWidget() instanceof Focusable) {
            ((Focusable) getWidget()).setTabIndex(((TabIndexState) state)
                    .getTabIndex());
        }

        if (getWidget() instanceof FocusWidget) {
            FocusWidget fw = (FocusWidget) getWidget();
            fw.setEnabled(isEnabled());
        }

        // Style names
        String styleName = getStyleNames(getWidget().getStylePrimaryName(),
                this);
        getWidget().setStyleName(styleName);

        // Update tooltip
        TooltipInfo tooltipInfo = paintableMap.getTooltipInfo(this, null);
        if (getState().hasDescription()) {
            tooltipInfo.setTitle(getState().getDescription());
        } else {
            tooltipInfo.setTitle(null);
        }
        // add error info to tooltip if present
        tooltipInfo.setErrorMessage(getState().getErrorMessage());

        // Set captions
        if (delegateCaptionHandling()) {
            ComponentContainerConnector parent = getParent();
            if (parent != null) {
                parent.updateCaption(this);
            } else {
                VConsole.error("Parent of connector "
                        + getClass().getName()
                        + " ("
                        + getConnectorId()
                        + ") is null. This is typically an indication of a broken component hierarchy");
            }
        }

        /*
         * updateComponentSize need to be after caption update so caption can be
         * taken into account
         */

        updateComponentSize();
    }

    @Deprecated
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // TODO Remove this method
    }

    private void updateComponentSize() {
        String newWidth = getState().getWidth();
        String newHeight = getState().getHeight();

        // Parent should be updated if either dimension changed between relative
        // and non-relative
        if (newWidth.endsWith("%") != lastKnownWidth.endsWith("%")) {
            ComponentContainerConnector parent = getParent();
            if (parent instanceof ManagedLayout) {
                getLayoutManager().setWidthNeedsUpdate((ManagedLayout) parent);
            }
        }

        if (newHeight.endsWith("%") != lastKnownHeight.endsWith("%")) {
            ComponentContainerConnector parent = getParent();
            if (parent instanceof ManagedLayout) {
                getLayoutManager().setHeightNeedsUpdate((ManagedLayout) parent);
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

    public boolean isRelativeHeight() {
        return getState().getHeight().endsWith("%");
    }

    public boolean isRelativeWidth() {
        return getState().getWidth().endsWith("%");
    }

    public boolean isUndefinedHeight() {
        return getState().getHeight().length() == 0;
    }

    public boolean isUndefinedWidth() {
        return getState().getWidth().length() == 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.Connector#isEnabled()
     */
    public boolean isEnabled() {
        if (!getState().isEnabled()) {
            return false;
        }

        if (getParent() == null) {
            return true;
        } else {
            return getParent().isEnabled();
        }
    }

    /**
     * Return true if parent handles caption, false if the paintable handles the
     * caption itself.
     * 
     * 
     * @deprecated This should always return true and all components should let
     *             the parent handle the caption and use other attributes for
     *             internal texts in the component
     * @return
     */
    @Deprecated
    protected boolean delegateCaptionHandling() {
        return true;
    }

    /**
     * Generates the style name for the widget based on the given primary style
     * name (typically returned by Widget.getPrimaryStyleName()) and the UIDL
     * and shared state of the component. An additional "modified" style name
     * can be added if the field parameter is set to true.
     * 
     * @param primaryStyleName
     * @param uidl
     * @param state
     *            component shared state
     * @param field
     * @return
     */
    protected static String getStyleNames(String primaryStyleName,
            ComponentConnector connector) {
        ComponentState state = connector.getState();

        StringBuffer styleBuf = new StringBuffer();
        styleBuf.append(primaryStyleName);
        styleBuf.append(" v-paintable");

        // Uses connector methods to enable connectors to take hierarchy or
        // multiple state variables into account
        if (!connector.isEnabled()) {
            styleBuf.append(" ");
            styleBuf.append(ApplicationConnection.DISABLED_CLASSNAME);
        }
        if (connector.isReadOnly()) {
            styleBuf.append(" ");
            styleBuf.append("v-readonly");
        }

        // add additional styles as css classes, prefixed with component default
        // stylename
        if (state.hasStyles()) {
            for (String style : state.getStyles()) {
                styleBuf.append(" ");
                styleBuf.append(primaryStyleName);
                styleBuf.append("-");
                styleBuf.append(style);
                styleBuf.append(" ");
                styleBuf.append(style);
            }
        }

        if (connector instanceof AbstractFieldConnector) {
            // TODO Move to AbstractFieldConnector
            AbstractFieldConnector afc = ((AbstractFieldConnector) connector);
            if (afc.isModified()) {
                // add modified classname to Fields
                styleBuf.append(" ");
                styleBuf.append(ApplicationConnection.MODIFIED_CLASSNAME);
            }

            if (afc.isRequired()) {
                // add required classname to required fields
                styleBuf.append(" ");
                styleBuf.append(primaryStyleName);
                styleBuf.append(ApplicationConnection.REQUIRED_CLASSNAME_EXT);
            }
        }

        // add error classname to components w/ error
        if (null != state.getErrorMessage()) {
            styleBuf.append(" ");
            styleBuf.append(primaryStyleName);
            styleBuf.append(ApplicationConnection.ERROR_CLASSNAME_EXT);
        }

        return styleBuf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ComponentConnector#isReadOnly()
     */
    @Deprecated
    public boolean isReadOnly() {
        return getState().isReadOnly();
    }

    /**
     * Sets the shared state for the paintable widget.
     * 
     * @param new shared state (must be compatible with the return value of
     *        {@link #getState()} - {@link ComponentState} if
     *        {@link #getState()} is not overridden
     */
    public final void setState(SharedState state) {
        this.state = (ComponentState) state;
    }

    public LayoutManager getLayoutManager() {
        return LayoutManager.get(getConnection());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.Connector#getParent()
     */
    public ComponentContainerConnector getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.Connector#setParent(com.vaadin.terminal
     * .gwt.client.ComponentContainerConnector)
     */
    public void setParent(ComponentContainerConnector parent) {
        this.parent = parent;
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
    public boolean hasEventListener(String eventIdentifier) {
        Set<String> reg = getState().getRegisteredEventListeners();
        return (reg != null && reg.contains(eventIdentifier));
    }

}
