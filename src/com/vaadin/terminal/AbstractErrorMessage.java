/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Buffered;
import com.vaadin.data.Validator;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

/**
 * Base class for component error messages.
 * 
 * This class is used on the server side to construct the error messages to send
 * to the client.
 * 
 * @since 7.0
 */
public abstract class AbstractErrorMessage implements ErrorMessage {

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
         * Content mode, where the error contains XHTML.
         */
        XHTML;
    }

    /**
     * Content mode.
     */
    private ContentMode mode = ContentMode.TEXT;

    /**
     * Message in content mode.
     */
    private String message;

    /**
     * Error level.
     */
    private ErrorLevel level = ErrorLevel.ERROR;

    private List<ErrorMessage> causes = new ArrayList<ErrorMessage>();

    protected AbstractErrorMessage(String message) {
        this.message = message;
    }

    protected String getMessage() {
        return message;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    /* Documented in interface */
    public ErrorLevel getErrorLevel() {
        return level;
    }

    protected void setErrorLevel(ErrorLevel level) {
        this.level = level;
    }

    protected ContentMode getMode() {
        return mode;
    }

    protected void setMode(ContentMode mode) {
        this.mode = mode;
    }

    protected List<ErrorMessage> getCauses() {
        return causes;
    }

    protected void addCause(ErrorMessage cause) {
        causes.add(cause);
    }

    @Deprecated
    public void paint(PaintTarget target) throws PaintException {

        // TODO if no message and only one cause, paint cause only? error level?

        target.startTag(AbstractComponentConnector.ATTRIBUTE_ERROR);
        target.addAttribute("level", level.getText());

        paintContent(target);

        target.endTag(AbstractComponentConnector.ATTRIBUTE_ERROR);
    }

    // TODO temporary method - move logic to client side
    @Deprecated
    protected void paintContent(PaintTarget target) throws PaintException {
        // Paint the message
        switch (getMode()) {
        case TEXT:
            target.addText(AbstractApplicationServlet
                    .safeEscapeForHtml(getMessage()));
            break;
        case PREFORMATTED:
            target.addText("<pre>"
                    + AbstractApplicationServlet
                            .safeEscapeForHtml(getMessage()) + "</pre>");
            break;
        case XHTML:
            target.addText(getMessage());
            break;
        }

        if (getCauses().size() > 0) {
            for (ErrorMessage cause : getCauses()) {
                cause.paint(target);
            }
        }
    }

    // TODO replace this with a helper method elsewhere?
    public static ErrorMessage getErrorMessageForException(Throwable t) {
        if (null == t) {
            return null;
        } else if (t instanceof ErrorMessage) {
            // legacy case for custom error messages
            return (ErrorMessage) t;
        } else if (t instanceof Validator.InvalidValueException) {
            UserError error = new UserError(
                    ((Validator.InvalidValueException) t).getHtmlMessage(),
                    ContentMode.XHTML, ErrorLevel.ERROR);
            for (Validator.InvalidValueException nestedException : ((Validator.InvalidValueException) t)
                    .getCauses()) {
                error.addCause(getErrorMessageForException(nestedException));
            }
            return error;
        } else if (t instanceof Buffered.SourceException) {
            // no message, only the causes to be painted
            UserError error = new UserError(null);
            // in practice, this was always ERROR in Vaadin 6 unless tweaked in
            // custom exceptions implementing ErrorMessage
            error.setErrorLevel(ErrorLevel.ERROR);
            // causes
            for (Throwable nestedException : ((Buffered.SourceException) t)
                    .getCauses()) {
                error.addCause(getErrorMessageForException(nestedException));
            }
            return error;
        } else {
            return new SystemError(t);
        }
    }

    /* Documented in superclass */
    @Override
    public String toString() {
        return getMessage();
    }

}
