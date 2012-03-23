/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.terminal.gwt.client.communication.SharedState;
import com.vaadin.terminal.gwt.client.communication.URLReference;
import com.vaadin.ui.Component;

/**
 * Default shared state implementation for UI components.
 * 
 * State classes of concrete components should extend this class.
 * 
 * @since 7.0
 */
public class ComponentState extends SharedState {
    private String height = "";
    private String width = "";
    private boolean readOnly = false;
    private boolean immediate = false;
    private boolean enabled = true;
    private String description = "";
    // Note: for the caption, there is a difference between null and an empty
    // string!
    private String caption = null;
    private boolean visible = true;
    private URLReference icon = null;
    private List<String> styles = null;
    private String debugId = null;
    /**
     * A set of event identifiers with registered listeners.
     */
    private Set<String> registeredEventListeners = null;

    // HTML formatted error message for the component
    // TODO this could be an object with more information, but currently the UI
    // only uses the message
    private String errorMessage = null;

    /**
     * Returns the component height as set by the server.
     * 
     * Can be relative (containing the percent sign) or absolute, or empty
     * string for undefined height.
     * 
     * @return component height as defined by the server, not null
     */
    public String getHeight() {
        if (height == null) {
            return "";
        }
        return height;
    }

    /**
     * Sets the height of the component in the server format.
     * 
     * Can be relative (containing the percent sign) or absolute, or null or
     * empty string for undefined height.
     * 
     * @param height
     *            component height
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * Returns true if the component height is undefined, false if defined
     * (absolute or relative).
     * 
     * @return true if component height is undefined
     */
    public boolean isUndefinedHeight() {
        return "".equals(getHeight());
    }

    /**
     * Returns the component width as set by the server.
     * 
     * Can be relative (containing the percent sign) or absolute, or empty
     * string for undefined height.
     * 
     * @return component width as defined by the server, not null
     */
    public String getWidth() {
        if (width == null) {
            return "";
        }
        return width;
    }

    /**
     * Sets the width of the component in the server format.
     * 
     * Can be relative (containing the percent sign) or absolute, or null or
     * empty string for undefined width.
     * 
     * @param width
     *            component width
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * Returns true if the component width is undefined, false if defined
     * (absolute or relative).
     * 
     * @return true if component width is undefined
     */
    public boolean isUndefinedWidth() {
        return "".equals(getWidth());
    }

    /**
     * Returns true if the component is in read-only mode.
     * 
     * @see com.vaadin.ui.Component#isReadOnly()
     * 
     * @return true if the component is in read-only mode
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets or resets the read-only mode for a component.
     * 
     * @see com.vaadin.ui.Component#setReadOnly()
     * 
     * @param readOnly
     *            new mode for the component
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Returns true if the component is in immediate mode.
     * 
     * @see com.vaadin.terminal.VariableOwner#isImmediate()
     * 
     * @return true if the component is in immediate mode
     */
    public boolean isImmediate() {
        return immediate;
    }

    /**
     * Sets or resets the immediate mode for a component.
     * 
     * @see com.vaadin.terminal.VariableOwner#setImmediate()
     * 
     * @param immediate
     *            new mode for the component
     */
    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    /**
     * Returns true if the component has user-defined styles.
     * 
     * @return true if the component has user-defined styles
     */
    public boolean hasStyles() {
        return styles != null && !styles.isEmpty();
    }

    /**
     * Returns true if the component is enabled.
     * 
     * @see com.vaadin.ui.Component#isEnabled()
     * 
     * @return true if the component is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables or disables the component.
     * 
     * @see com.vaadin.ui.Component#setEnabled(boolean)
     * 
     * @param enabled
     *            new mode for the component
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the description of the component (typically shown as tooltip).
     * 
     * @see com.vaadin.ui.AbstractComponent#getDescription()
     * 
     * @return component description (not null, can be empty string)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the component (typically shown as tooltip).
     * 
     * @see com.vaadin.ui.AbstractComponent#setDescription(String)
     * 
     * @param description
     *            new component description (can be null)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns true if the component has a description.
     * 
     * @return true if the component has a description
     */
    public boolean hasDescription() {
        return getDescription() != null && !"".equals(getDescription());
    }

    /**
     * Gets the caption of the component (typically shown by the containing
     * layout).
     * 
     * @see com.vaadin.ui.Component#getCaption()
     * 
     * @return component caption - can be null (no caption) or empty string
     *         (reserve space for an empty caption)
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption of the component (typically shown by the containing
     * layout).
     * 
     * @see com.vaadin.ui.Component#setCaption(String)
     * 
     * @param caption
     *            new component caption - can be null (no caption) or empty
     *            string (reserve space for an empty caption)
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Returns the visibility state of the component. Note that this state is
     * related to the component only, not its parent. This might differ from
     * what {@link Component#isVisible()} returns as this takes the hierarchy
     * into account.
     * 
     * @return The visibility state.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visibility state of the component.
     * 
     * @param visible
     *            The new visibility state.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public URLReference getIcon() {
        return icon;
    }

    public void setIcon(URLReference icon) {
        this.icon = icon;
    }

    /**
     * Gets the style names for the component.
     * 
     * @return A List of style names or null if no styles have been set.
     */
    public List<String> getStyles() {
        return styles;
    }

    /**
     * Sets the style names for the component.
     * 
     * @param styles
     *            A list containing style names
     */
    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

    /**
     * Gets the debug id for the component. The debugId is added as DOM id for
     * the component.
     * 
     * @return The debug id for the component or null if not set
     */
    public String getDebugId() {
        return debugId;
    }

    /**
     * Sets the debug id for the component. The debugId is added as DOM id for
     * the component.
     * 
     * @param debugId
     *            The new debugId for the component or null for no debug id
     * 
     */
    public void setDebugId(String debugId) {
        this.debugId = debugId;
    }

    /**
     * Gets the identifiers for the event listeners that have been registered
     * for the component (using an event id)
     * 
     * @return A set of event identifiers or null if no identifiers have been
     *         registered
     */
    public Set<String> getRegisteredEventListeners() {
        return registeredEventListeners;
    }

    /**
     * Sets the identifiers for the event listeners that have been registered
     * for the component (using an event id)
     * 
     * @param registeredEventListeners
     *            The new set of identifiers or null if no identifiers have been
     *            registered
     */
    public void setRegisteredEventListeners(Set<String> registeredEventListeners) {
        this.registeredEventListeners = registeredEventListeners;
    }

    /**
     * Adds an event listener id.
     * 
     * @param eventListenerId
     *            The event identifier to add
     */
    public void addRegisteredEventListener(String eventListenerId) {
        if (registeredEventListeners == null) {
            registeredEventListeners = new HashSet<String>();
        }
        registeredEventListeners.add(eventListenerId);

    }

    /**
     * Removes an event listener id.
     * 
     * @param eventListenerId
     *            The event identifier to remove
     */
    public void removeRegisteredEventListener(String eventIdentifier) {
        if (registeredEventListeners == null) {
            return;
        }
        registeredEventListeners.remove(eventIdentifier);
        if (registeredEventListeners.size() == 0) {
            registeredEventListeners = null;
        }
    }

    /**
     * Returns the current error message for the component.
     * 
     * @return HTML formatted error message to show for the component or null if
     *         none
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the current error message for the component.
     * 
     * TODO this could use an object with more details about the error
     * 
     * @param errorMessage
     *            HTML formatted error message to show for the component or null
     *            for none
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
