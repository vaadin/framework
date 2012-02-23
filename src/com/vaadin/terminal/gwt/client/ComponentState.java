/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.vaadin.terminal.gwt.client.communication.SharedState;

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
    private String style = "";
    private boolean disabled = false;

    // TODO more fields to move here: caption and description

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
     * Returns the component styles as set by the server, as a space separated
     * string.
     * 
     * @return component styles as defined by the server, not null
     */
    public String getStyle() {
        if (style == null) {
            return "";
        }
        return style;
    }

    /**
     * Sets the component styles as a space separated string.
     * 
     * @param style
     *            component styles as a space separated string, not null
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Returns true if the component has user-defined styles.
     * 
     * @return true if the component has user-defined styles
     */
    public boolean hasStyles() {
        return !"".equals(getStyle());
    }

    /**
     * Returns true if the component is disabled.
     * 
     * @see com.vaadin.ui.Component#isEnabled()
     * 
     * @return true if the component is disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Disables or enables the component.
     * 
     * @see com.vaadin.ui.Component#setEnabled(boolean)
     * 
     * @param disabled
     *            new mode for the component
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
