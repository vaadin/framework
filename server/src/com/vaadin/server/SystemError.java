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
 * <code>SystemError</code> is an error message for a problem caused by error in
 * system, not the user application code. The system error can contain technical
 * information such as stack trace and exception.
 * 
 * SystemError does not support HTML in error messages or stack traces. If HTML
 * messages are required, use {@link UserError} or a custom implementation of
 * {@link ErrorMessage}.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class SystemError extends AbstractErrorMessage {

    /**
     * Constructor for SystemError with error message specified.
     * 
     * @param message
     *            the Textual error description.
     */
    public SystemError(String message) {
        super(message);
        setErrorLevel(ErrorLevel.SYSTEMERROR);
        setMode(ContentMode.HTML);
        setMessage(getHtmlMessage());
    }

    /**
     * Constructor for SystemError with causing exception and error message.
     * 
     * @param message
     *            the Textual error description.
     * @param cause
     *            the throwable causing the system error.
     */
    public SystemError(String message, Throwable cause) {
        this(message);
        addCause(AbstractErrorMessage.getErrorMessageForException(cause));
    }

    /**
     * Constructor for SystemError with cause.
     * 
     * @param cause
     *            the throwable causing the system error.
     */
    public SystemError(Throwable cause) {
        this(null, cause);
    }

    /**
     * Returns the message of the error in HTML.
     * 
     * Note that this API may change in future versions.
     */
    protected String getHtmlMessage() {
        // TODO wrapping div with namespace? See the old code:
        // target.addXMLSection("div", message,
        // "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");

        StringBuilder sb = new StringBuilder();
        if (getMessage() != null) {
            sb.append("<h2>");
            sb.append(VaadinServlet.safeEscapeForHtml(getMessage()));
            sb.append("</h2>");
        }
        return sb.toString();
    }

}
