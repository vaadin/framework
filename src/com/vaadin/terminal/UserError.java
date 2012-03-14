/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

/**
 * <code>UserError</code> is a controlled error occurred in application. User
 * errors are occur in normal usage of the application and guide the user.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class UserError extends AbstractErrorMessage {

    /**
     * @deprecated from 7.0, use {@link ContentMode#TEXT} instead    
     */
    @Deprecated
    public static final ContentMode CONTENT_TEXT = ContentMode.TEXT;

    /**
     * @deprecated from 7.0, use {@link ContentMode#PREFORMATTED} instead    
     */
    @Deprecated
    public static final ContentMode CONTENT_PREFORMATTED = ContentMode.PREFORMATTED;

    /**
     * @deprecated from 7.0, use {@link ContentMode#XHTML} instead    
     */
    @Deprecated
    public static final ContentMode CONTENT_XHTML = ContentMode.XHTML;

    /**
     * Creates a textual error message of level ERROR.
     * 
     * @param textErrorMessage
     *            the text of the error message.
     */
    public UserError(String textErrorMessage) {
        super(textErrorMessage);
    }

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
