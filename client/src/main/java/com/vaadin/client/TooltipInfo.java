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
package com.vaadin.client;

import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.shared.util.SharedUtil;

public class TooltipInfo {

    private String title;

    private String errorMessageHtml;

    private ErrorLevel errorLevel;

    // Contains the tooltip's identifier. If a tooltip's contents and this
    // identifier haven't changed, the tooltip won't be updated in subsequent
    // events.
    private Object identifier;

    public TooltipInfo() {
    }

    /**
     * Constructs a new tooltip info instance.
     *
     * @param tooltip
     *         tooltip title
     */
    public TooltipInfo(String tooltip) {
        setTitle(tooltip);
    }

    /**
     * Constructs a new tooltip info instance.
     *
     * @param tooltip
     *         tooltip title
     * @param errorMessage
     *         error message
     */
    public TooltipInfo(String tooltip, String errorMessage) {
        this(tooltip, errorMessage, null);
    }

    /**
     * Constructs a new tooltip info instance.
     *
     * @param tooltip
     *         tooltip title
     * @param errorMessage
     *         error message
     * @param identifier
     *         the tooltip's identifier
     */
    public TooltipInfo(String tooltip, String errorMessage, Object identifier) {
        this(tooltip, errorMessage, identifier, null);
    }

    /**
     * Constructs a new tooltip info instance.
     *
     * @param tooltip
     *         tooltip title
     * @param errorMessage
     *         error message
     * @param identifier
     *         the tooltip's identifier
     * @param errorLevel
     *         error level
     */
    public TooltipInfo(String tooltip, String errorMessage, Object identifier,
            ErrorLevel errorLevel) {
        setIdentifier(identifier);
        setTitle(tooltip);
        setErrorMessage(errorMessage);
        setErrorLevel(errorLevel);
    }

    public void setIdentifier(Object identifier) {
        this.identifier = identifier;
    }

    public Object getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getErrorMessage() {
        return errorMessageHtml;
    }

    public void setErrorMessage(String errorMessage) {
        errorMessageHtml = errorMessage;
    }

    public ErrorLevel getErrorLevel() {
        return errorLevel;
    }

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

    public boolean equals(TooltipInfo other) {
        return (other != null && SharedUtil.equals(other.title, title)
                && SharedUtil.equals(other.errorMessageHtml, errorMessageHtml)
                && other.identifier == identifier);
    }
}
