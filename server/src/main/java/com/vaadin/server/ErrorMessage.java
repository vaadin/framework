/*
 * Copyright 2000-2022 Vaadin Ltd.
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

package com.vaadin.server;

import java.io.Serializable;

import com.vaadin.shared.ui.ErrorLevel;

/**
 * Interface for rendering error messages to terminal. All the visible errors
 * shown to user must implement this interface.
 *
 * @author Vaadin Ltd.
 * @since 3.0
 */
public interface ErrorMessage extends Serializable {

    /**
     * @deprecated As of 7.0, use {@link ErrorLevel#SYSTEM} instead    
     */
    @Deprecated
    public static final ErrorLevel SYSTEMERROR = ErrorLevel.SYSTEM;

    /**
     * @deprecated As of 7.0, use {@link ErrorLevel#CRITICAL} instead    
     */
    @Deprecated
    public static final ErrorLevel CRITICAL = ErrorLevel.CRITICAL;

    /**
     * @deprecated As of 7.0, use {@link ErrorLevel#ERROR} instead    
     */

    @Deprecated
    public static final ErrorLevel ERROR = ErrorLevel.ERROR;

    /**
     * @deprecated As of 7.0, use {@link ErrorLevel#WARNING} instead    
     */
    @Deprecated
    public static final ErrorLevel WARNING = ErrorLevel.WARNING;

    /**
     * @deprecated As of 7.0, use {@link ErrorLevel#INFO} instead    
     */
    @Deprecated
    public static final ErrorLevel INFORMATION = ErrorLevel.INFO;

    /**
     * Gets the errors level.
     *
     * @return the level of error as an integer.
     */
    public ErrorLevel getErrorLevel();

    /**
     * Returns the HTML formatted message to show in as the error message on the
     * client.
     *
     * This method should perform any necessary escaping to avoid XSS attacks.
     *
     * TODO this API may still change to use a separate data transfer object
     *
     * @return HTML formatted string for the error message
     * @since 7.0
     */
    public String getFormattedHtmlMessage();

}
