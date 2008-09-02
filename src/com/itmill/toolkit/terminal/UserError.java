/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal;

/**
 * <code>UserError</code> is a controlled error occurred in application. User
 * errors are occur in normal usage of the application and guide the user.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
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
    private int level = ErrorMessage.ERROR;

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
     */
    public UserError(String message, int contentMode, int errorLevel) {

        // Check the parameters
        if (contentMode < 0 || contentMode > 2) {
            throw new java.lang.IllegalArgumentException(
                    "Unsupported content mode: " + contentMode);
        }

        msg = message;
        mode = contentMode;
        level = errorLevel;
    }

    /* Documenten in interface */
    public int getErrorLevel() {
        return level;
    }

    /* Documenten in interface */
    public void addListener(RepaintRequestListener listener) {
    }

    /* Documenten in interface */
    public void removeListener(RepaintRequestListener listener) {
    }

    /* Documenten in interface */
    public void requestRepaint() {
    }

    /* Documenten in interface */
    public void paint(PaintTarget target) throws PaintException {

        target.startTag("error");

        // Error level
        if (level >= ErrorMessage.SYSTEMERROR) {
            target.addAttribute("level", "system");
        } else if (level >= ErrorMessage.CRITICAL) {
            target.addAttribute("level", "critical");
        } else if (level >= ErrorMessage.ERROR) {
            target.addAttribute("level", "error");
        } else if (level >= ErrorMessage.WARNING) {
            target.addAttribute("level", "warning");
        } else {
            target.addAttribute("level", "info");
        }

        // Paint the message
        switch (mode) {
        case CONTENT_TEXT:
            target.addText(msg);
            break;
        case CONTENT_UIDL:
            target.addUIDL(msg);
            break;
        case CONTENT_PREFORMATTED:
            target.startTag("pre");
            target.addText(msg);
            target.endTag("pre");
        }

        target.endTag("error");
    }

    /* Documenten in interface */
    public void requestRepaintRequests() {
    }

    /* Documented in superclass */
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
