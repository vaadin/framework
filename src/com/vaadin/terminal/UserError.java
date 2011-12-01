/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

/**
 * <code>UserError</code> is a controlled error occurred in application. User
 * errors are occur in normal usage of the application and guide the user.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class UserError implements ErrorMessage {

    /**
     * Content mode, where the error contains only plain text.
     */
    public static final int CONTENT_TEXT = 0;

    /**
     * Content mode, where the error contains preformatted text.
     */
    public static final int CONTENT_PREFORMATTED = 1;

    /**
     * Formatted content mode, where the contents is XML restricted to the UIDL
     * 1.0 formatting markups.
     */
    public static final int CONTENT_UIDL = 2;

    /**
     * Content mode, where the error contains XHTML.
     */
    public static final int CONTENT_XHTML = 3;

    /**
     * Content mode.
     */
    private int mode = CONTENT_TEXT;

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

    /**
     * Creates a error message with level and content mode.
     * 
     * @param message
     *            the error message.
     * @param contentMode
     *            the content Mode.
     * @param errorLevel
     *            the level of error.
     * @deprecated use {link
     *             {@link #UserError(String, int, com.vaadin.terminal.ErrorMessage.ErrorLevel)}
     *             instead.
     */
    @Deprecated
    public UserError(String message, int contentMode, int errorLevel) {

        // Check the parameters
        if (contentMode < 0 || contentMode > 2) {
            throw new java.lang.IllegalArgumentException(
                    "Unsupported content mode: " + contentMode);
        }

        msg = message;
        mode = contentMode;
        if (errorLevel == ErrorLevel.INFORMATION.ordinal()) {
            level = ErrorLevel.INFORMATION;
        } else if (errorLevel == ErrorLevel.WARNING.ordinal()) {
            level = ErrorLevel.WARNING;
        } else if (errorLevel == ErrorLevel.ERROR.ordinal()) {
            level = ErrorLevel.ERROR;
        } else if (errorLevel == ErrorLevel.CRITICAL.ordinal()) {
            level = ErrorLevel.CRITICAL;
        } else {
            level = ErrorLevel.SYSTEMERROR;
        }
    }

    public UserError(String message, int contentMode, ErrorLevel errorLevel) {

        // Check the parameters
        if (contentMode < 0 || contentMode > 2) {
            throw new java.lang.IllegalArgumentException(
                    "Unsupported content mode: " + contentMode);
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

        // Error level
        if (level == ErrorLevel.INFORMATION) {
            target.addAttribute("level", "info");
        } else if (level == ErrorLevel.WARNING) {
            target.addAttribute("level", "warning");
        } else if (level == ErrorLevel.ERROR) {
            target.addAttribute("level", "error");
        } else if (level == ErrorLevel.CRITICAL) {
            target.addAttribute("level", "critical");
        } else {
            target.addAttribute("level", "system");
        }

        // Paint the message
        switch (mode) {
        case CONTENT_TEXT:
            target.addText(AbstractApplicationServlet.safeEscapeForHtml(msg));
            break;
        case CONTENT_UIDL:
            target.addUIDL(msg);
            break;
        case CONTENT_PREFORMATTED:
            target.addText("<pre>"
                    + AbstractApplicationServlet.safeEscapeForHtml(msg)
                    + "</pre>");
            break;
        case CONTENT_XHTML:
            target.addText(msg);
            break;
        }

        target.endTag("error");
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
