/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import com.vaadin.terminal.gwt.client.communication.SharedState;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;
import com.vaadin.terminal.gwt.server.ClientMethodInvocation;

/**
 * <code>SystemError</code> is a runtime exception caused by error in system.
 * The system error can be shown to the user as it implements
 * <code>ErrorMessage</code> interface, but contains technical information such
 * as stack trace and exception.
 * 
 * SystemError does not support HTML in error messages or stack traces. If HTML
 * messages are required, use {@link UserError} or a custom implementation of
 * {@link ErrorMessage}.
 * 
 * @author Vaadin Ltd.
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
     * @see com.vaadin.terminal.ErrorMessage#getErrorLevel()
     */
    public final ErrorLevel getErrorLevel() {
        return ErrorLevel.SYSTEMERROR;
    }

    /**
     * @see com.vaadin.terminal.Paintable#paint(com.vaadin.terminal.PaintTarget)
     */
    public void paint(PaintTarget target) throws PaintException {

        target.startTag("error");
        target.addAttribute("level", ErrorLevel.SYSTEMERROR.getText());

        String message = getHtmlMessage();

        target.addXMLSection("div", message,
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");

        target.endTag("error");

    }

    public SharedState getState() {
        // TODO implement: move relevant parts from paint() to getState()
        return null;
    }

    public List<ClientMethodInvocation> retrievePendingRpcCalls() {
        return Collections.emptyList();
    }

    /**
     * Returns the message of the error in HTML.
     * 
     * Note that this API may change in future versions.
     */
    protected String getHtmlMessage() {
        StringBuilder sb = new StringBuilder();
        final String message = getLocalizedMessage();
        if (message != null) {
            sb.append("<h2>");
            sb.append(AbstractApplicationServlet.safeEscapeForHtml(message));
            sb.append("</h2>");
        }

        // Paint the exception
        if (cause != null) {
            sb.append("<h3>Exception</h3>");
            final StringWriter buffer = new StringWriter();
            cause.printStackTrace(new PrintWriter(buffer));
            sb.append("<pre>");
            sb.append(AbstractApplicationServlet.safeEscapeForHtml(buffer
                    .toString()));
            sb.append("</pre>");
        }
        return sb.toString();
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
