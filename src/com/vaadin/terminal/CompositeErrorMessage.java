/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.vaadin.terminal.gwt.client.communication.SharedState;

/**
 * Class for combining multiple error messages together.
 * 
 * @author Vaadin Ltd
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class CompositeErrorMessage implements ErrorMessage, Serializable {

    /**
     * Array of all the errors.
     */
    private final List<ErrorMessage> errors;

    /**
     * Level of the error.
     */
    private ErrorLevel level;

    /**
     * Constructor for CompositeErrorMessage.
     * 
     * @param errorMessages
     *            the Array of error messages that are listed togeter. Nulls are
     *            ignored, but at least one message is required.
     */
    public CompositeErrorMessage(ErrorMessage[] errorMessages) {
        errors = new ArrayList<ErrorMessage>(errorMessages.length);
        level = ErrorLevel.INFORMATION;

        for (int i = 0; i < errorMessages.length; i++) {
            addErrorMessage(errorMessages[i]);
        }

        if (errors.size() == 0) {
            throw new IllegalArgumentException(
                    "Composite error message must have at least one error");
        }

    }

    /**
     * Constructor for CompositeErrorMessage.
     * 
     * @param errorMessages
     *            the Collection of error messages that are listed together. At
     *            least one message is required.
     */
    public CompositeErrorMessage(
            Collection<? extends ErrorMessage> errorMessages) {
        errors = new ArrayList<ErrorMessage>(errorMessages.size());
        level = ErrorLevel.INFORMATION;

        for (final Iterator<? extends ErrorMessage> i = errorMessages
                .iterator(); i.hasNext();) {
            addErrorMessage(i.next());
        }

        if (errors.size() == 0) {
            throw new IllegalArgumentException(
                    "Composite error message must have at least one error");
        }
    }

    /**
     * The error level is the largest error level in
     * 
     * @see com.vaadin.terminal.ErrorMessage#getErrorLevel()
     */
    public final ErrorLevel getErrorLevel() {
        return level;
    }

    /**
     * Adds a error message into this composite message. Updates the level
     * field.
     * 
     * @param error
     *            the error message to be added. Duplicate errors are ignored.
     */
    private void addErrorMessage(ErrorMessage error) {
        if (error != null && !errors.contains(error)) {
            errors.add(error);
            if (error.getErrorLevel().intValue() > level.intValue()) {
                level = error.getErrorLevel();
            }
        }
    }

    /**
     * Gets Error Iterator.
     * 
     * @return the error iterator.
     */
    public Iterator<ErrorMessage> iterator() {
        return errors.iterator();
    }

    /**
     * @see com.vaadin.terminal.Paintable#paint(com.vaadin.terminal.PaintTarget)
     */
    public void paint(PaintTarget target) throws PaintException {

        if (errors.size() == 1) {
            (errors.iterator().next()).paint(target);
        } else {
            target.startTag("error");
            target.addAttribute("level", level.getText());

            // Paint all the exceptions
            for (final Iterator<ErrorMessage> i = errors.iterator(); i
                    .hasNext();) {
                i.next().paint(target);
            }

            target.endTag("error");
        }
    }

    public SharedState getState() {
        // TODO implement: move relevant parts from paint() to getState()
        return null;
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

    /**
     * Returns a comma separated list of the error messages.
     * 
     * @return String, comma separated list of error messages.
     */
    @Override
    public String toString() {
        String retval = "[";
        int pos = 0;
        for (final Iterator<ErrorMessage> i = errors.iterator(); i.hasNext();) {
            if (pos > 0) {
                retval += ",";
            }
            pos++;
            retval += i.next().toString();
        }
        retval += "]";

        return retval;
    }

    public String getDebugId() {
        return null;
    }

    public void setDebugId(String id) {
        throw new UnsupportedOperationException(
                "Setting testing id for this Paintable is not implemented");
    }
}
