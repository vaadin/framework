/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <code>SystemError</code> is a runtime exception caused by error in system.
 * The system error can be shown to the user as it implements
 * <code>ErrorMessage</code> interface, but contains technical information such
 * as stack trace and exception.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class SystemError extends RuntimeException implements ErrorMessage {

    /**
     * The cause of the system error. The cause is stored separately as JDK 1.3
     * does not support causes natively.
     */
    private Throwable cause = null;

    /**
     * Constructor for SystemError with error message specified.
     * 
     * @param message
     *            the Textual error description.
     */
    public SystemError(String message) {
        super(message);
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
        super(message);
        this.cause = cause;
    }

    /**
     * Constructor for SystemError with cause.
     * 
     * @param cause
     *            the throwable causing the system error.
     */
    public SystemError(Throwable cause) {
        this.cause = cause;
    }

    /**
     * @see com.itmill.toolkit.terminal.ErrorMessage#getErrorLevel()
     */
    public final int getErrorLevel() {
        return ErrorMessage.SYSTEMERROR;
    }

    /**
     * @see com.itmill.toolkit.terminal.Paintable#paint(com.itmill.toolkit.terminal.PaintTarget)
     */
    public void paint(PaintTarget target) throws PaintException {

        target.startTag("error");
        target.addAttribute("level", "system");

        // Paint the error message
        final String message = getLocalizedMessage();
        if (message != null) {
            target.addSection("h2", message);
        }

        // Paint the exception
        if (cause != null) {
            target.addSection("h3", "Exception");
            final StringWriter buffer = new StringWriter();
            cause.printStackTrace(new PrintWriter(buffer));
            target.addSection("pre", buffer.toString());
        }

        target.endTag("error");

    }

    /**
     * Gets cause for the error.
     * 
     * @return the cause.
     * @see java.lang.Throwable#getCause()
     */
    @Override
    public Throwable getCause() {
        return cause;
    }

    /* Documented in super interface */
    public void addListener(RepaintRequestListener listener) {
    }

    /* Documented in super interface */
    public void removeListener(RepaintRequestListener listener) {
    }

    /* Documented in super interface */
    public void requestRepaint() {
    }

    /* Documented in super interface */
    public void requestRepaintRequests() {
    }

    public String getDebugId() {
        return null;
    }

    public void setDebugId(String id) {
        throw new UnsupportedOperationException(
                "Setting testing id for this Paintable is not implemented");
    }

}
