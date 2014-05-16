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

/**
 * <code>UserError</code> is a controlled error occurred in application. User
 * errors are occur in normal usage of the application and guide the user.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class UserError extends AbstractErrorMessage {

    /**
     * @deprecated As of 7.0, use {@link ContentMode#TEXT} instead    
     */
    @Deprecated
    public static final ContentMode CONTENT_TEXT = ContentMode.TEXT;

    /**
     * @deprecated As of 7.0, use {@link ContentMode#PREFORMATTED} instead    
     */
    @Deprecated
    public static final ContentMode CONTENT_PREFORMATTED = ContentMode.PREFORMATTED;

    /**
     * @deprecated As of 7.0, use {@link ContentMode#HTML} instead    
     */
    @Deprecated
    public static final ContentMode CONTENT_XHTML = ContentMode.HTML;

    /**
     * Creates a textual error message of level ERROR.
     * 
     * @param textErrorMessage
     *            the text of the error message.
     */
    public UserError(String textErrorMessage) {
        super(textErrorMessage);
    }

    /**
     * Creates an error message with level and content mode.
     * 
     * @param message
     *            the error message.
     * @param contentMode
     *            the content Mode.
     * @param errorLevel
     *            the level of error.
     */
    public UserError(String message, ContentMode contentMode,
            ErrorLevel errorLevel) {
        super(message);
        if (contentMode == null) {
            contentMode = ContentMode.TEXT;
        }
        if (errorLevel == null) {
            errorLevel = ErrorLevel.ERROR;
        }
        setMode(contentMode);
        setErrorLevel(errorLevel);
    }

}
