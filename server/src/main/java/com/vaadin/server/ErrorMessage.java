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

package com.vaadin.server;

import java.io.Serializable;

/**
 * Interface for rendering error messages to terminal. All the visible errors
 * shown to user must implement this interface.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
public interface ErrorMessage extends Serializable {

    public enum ErrorLevel {
        /**
         * Error code for informational messages.
         */
        INFORMATION("info", 0),
        /**
         * Error code for warning messages.
         */
        WARNING("warning", 1),
        /**
         * Error code for regular error messages.
         */
        ERROR("error", 2),
        /**
         * Error code for critical error messages.
         */
        CRITICAL("critical", 3),
        /**
         * Error code for system errors and bugs.
         */
        SYSTEMERROR("system", 4);

        String text;
        int errorLevel;

        private ErrorLevel(String text, int errorLevel) {
            this.text = text;
            this.errorLevel = errorLevel;
        }

        /**
         * Textual representation for server-client communication of level
         * 
         * @return String for error severity
         */
        public String getText() {
            return text;
        }

        /**
         * Integer representation of error severity for comparison
         * 
         * @return integer for error severity
         */
        public int intValue() {
            return errorLevel;
        }

        @Override
        public String toString() {
            return text;
        }

    }

    /**
     * @deprecated As of 7.0, use {@link ErrorLevel#SYSTEMERROR} instead    
     */
    @Deprecated
    public static final ErrorLevel SYSTEMERROR = ErrorLevel.SYSTEMERROR;

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
     * @deprecated As of 7.0, use {@link ErrorLevel#INFORMATION} instead    
     */
    @Deprecated
    public static final ErrorLevel INFORMATION = ErrorLevel.INFORMATION;

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
