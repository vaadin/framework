/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client;

import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.util.SharedUtil;

/**
 * An object that contains information about a tooltip, such as the tooltip's
 * title, error message, error level and an ID.
 */
public class TooltipInfo {

    private String title;

    private String errorMessageHtml;

    private ErrorLevel errorLevel;

    private ContentMode contentMode = ContentMode.HTML;

    // Contains the tooltip's identifier. If a tooltip's contents and this
    // identifier haven't changed, the tooltip won't be updated in subsequent
    // events.
    private Object identifier;

    /**
     * Constructs a new tooltip info instance.
     */
    public TooltipInfo() {
    }

    /**
     * Constructs a new tooltip info instance.
     *
     * @param tooltip
     *            tooltip title
     */
    public TooltipInfo(String tooltip) {
        setTitle(tooltip);
    }

    /**
     * Constructs a new tooltip info instance.
     *
     * @param tooltip
     *            tooltip title
     * @param errorMessage
     *            error message
     */
    public TooltipInfo(String tooltip, String errorMessage) {
        this(tooltip, errorMessage, null);
    }

    /**
     * Constructs a new tooltip info instance.
     *
     * @param tooltip
     *            tooltip title
     * @param errorMessage
     *            error message
     * @param identifier
     *            the tooltip's identifier
     */
    public TooltipInfo(String tooltip, String errorMessage, Object identifier) {
        this(tooltip, errorMessage, identifier, null);
    }

    /**
     * Constructs a new tooltip info instance.
     *
     * @param tooltip
     *            tooltip title
     * @param errorMessage
     *            error message
     * @param identifier
     *            the tooltip's identifier
     * @param errorLevel
     *            error level
     *
     * @since 7.7.11
     */
    public TooltipInfo(String tooltip, String errorMessage, Object identifier,
            ErrorLevel errorLevel) {
        setIdentifier(identifier);
        setTitle(tooltip);
        setErrorMessage(errorMessage);
        setErrorLevel(errorLevel);
    }

    /**
     * Sets the tooltip's identifier.
     *
     * @param identifier
     *            the identifier to set
     */
    public void setIdentifier(Object identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the tooltip's identifier.
     *
     * @return the identifier
     */
    public Object getIdentifier() {
        return identifier;
    }

    /**
     * Gets the tooltip title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the tooltip title.
     *
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessageHtml;
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage
     *            the error message to set
     */
    public void setErrorMessage(String errorMessage) {
        errorMessageHtml = errorMessage;
    }

    /**
     * Gets the error level.
     *
     * @return the error level
     * @since 7.7.11
     */
    public ErrorLevel getErrorLevel() {
        return errorLevel;
    }

    /**
     * Sets the error level.
     *
     * @param errorLevel
     *            the error level to set
     * @since 7.7.11
     */
    public void setErrorLevel(ErrorLevel errorLevel) {
        this.errorLevel = errorLevel;
    }

    /**
     * Checks is a message has been defined for the tooltip.
     *
     * @return true if title or error message is present, false if both are
     *         empty
     */
    public boolean hasMessage() {
        return (title != null && !title.isEmpty())
                || (errorMessageHtml != null && !errorMessageHtml.isEmpty());
    }

    /**
     * Indicates whether another tooltip info instance is equal to this one. Two
     * instances are equal if their title, error message, error level and
     * identifier are equal.
     *
     * @param other
     *            the reference tooltip info instance with which to compare
     * @return {@code true} if the instances are equal, {@code false} otherwise
     */
    public boolean equals(TooltipInfo other) {
        return (other != null && SharedUtil.equals(other.title, title)
                && SharedUtil.equals(other.errorMessageHtml, errorMessageHtml)
                && SharedUtil.equals(other.errorLevel, errorLevel)
                && other.identifier == identifier);
    }

    /**
     * Gets the tooltip title's content mode.
     * 
     * @since 7.7.14
     *
     * @return the content mode
     */
    public ContentMode getContentMode() {
        return contentMode;
    }

    /**
     * Sets the tooltip title's content mode.
     *
     * @since 7.7.14
     * 
     * @param contentMode
     *            the content mode to set
     */
    public void setContentMode(ContentMode contentMode) {
        this.contentMode = contentMode;
    }
}
