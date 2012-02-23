/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import com.vaadin.terminal.gwt.client.communication.SharedState;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

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
public class UserError implements ErrorMessage {

    public enum ContentMode {
        /**
         * Content mode, where the error contains only plain text.
         */
        TEXT,
        /**
         * Content mode, where the error contains preformatted text.
         */
        PREFORMATTED,
        /**
         * Formatted content mode, where the contents is XML restricted to the
         * UIDL 1.0 formatting markups.
         */
        UIDL,
        /**
         * Content mode, where the error contains XHTML.
         */
        XHTML;
    }

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
     * @deprecated from 7.0, use {@link ContentMode#UIDL} instead    
     */
    @Deprecated
    public static final ContentMode CONTENT_UIDL = ContentMode.UIDL;

    /**
     * @deprecated from 7.0, use {@link ContentMode#XHTML} instead    
     */
    @Deprecated
    public static final ContentMode CONTENT_XHTML = ContentMode.XHTML;

    /**
     * Content mode.
     */
    private ContentMode mode = ContentMode.TEXT;

    /**
     * Message in content mode.
     */
    private final String msg;

    /**
     * Error level.
     */
    private ErrorLevel level = ErrorLevel.ERROR;

    /**
     * Creates a textual error message of level ERROR.
     * 
     * @param textErrorMessage
     *            the text of the error message.
     */
    public UserError(String textErrorMessage) {
        msg = textErrorMessage;
    }

    public UserError(String message, ContentMode contentMode,
            ErrorLevel errorLevel) {
        if (contentMode == null) {
            contentMode = ContentMode.TEXT;
        }
        if (errorLevel == null) {
            errorLevel = ErrorLevel.ERROR;
        }
        msg = message;
        mode = contentMode;
        level = errorLevel;
    }

    /* Documented in interface */
    public ErrorLevel getErrorLevel() {
        return level;
    }

    /* Documented in interface */
    public void addListener(RepaintRequestListener listener) {
    }

    /* Documented in interface */
    public void removeListener(RepaintRequestListener listener) {
    }

    /* Documented in interface */
    public void requestRepaint() {
    }

    /* Documented in interface */
    public void paint(PaintTarget target) throws PaintException {

        target.startTag("error");
        target.addAttribute("level", level.getText());

        // Paint the message
        switch (mode) {
        case TEXT:
            target.addText(AbstractApplicationServlet.safeEscapeForHtml(msg));
            break;
        case UIDL:
            target.addUIDL(msg);
            break;
        case PREFORMATTED:
            target.addText("<pre>"
                    + AbstractApplicationServlet.safeEscapeForHtml(msg)
                    + "</pre>");
            break;
        case XHTML:
            target.addText(msg);
            break;
        }
        target.endTag("error");
    }

    public SharedState getState() {
        // TODO implement: move relevant parts from paint() to getState()
        return null;
    }

    /* Documented in interface */
    public void requestRepaintRequests() {
    }

    /* Documented in superclass */
    @Override
    public String toString() {
        return msg;
    }

    public String getDebugId() {
        return null;
    }

    public void setDebugId(String id) {
        throw new UnsupportedOperationException(
                "Setting testing id for this Paintable is not implemented");
    }

}
