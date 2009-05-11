/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.data;

import java.io.Serializable;

import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * Object validator interface. Implementors of this class can be added to any
 * {@link com.vaadin.data.Validatable} object to verify its value. The
 * <code>Validatable#isValid(Object)</code> iterates all registered
 * <code>Validator</code>s, calling their {@link #validate(Object)} methods.
 * <code>validate(Object)</code> should throw the
 * {@link Validator.InvalidValueException} if the given value is not valid by
 * its standards.
 * 
 * Validators should not have side effects on other objects as they can be
 * called from Paintable.paint().
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Validator extends Serializable {

    /**
     * Checks the given value against this validator. If the value is valid this
     * method should do nothing, and if it's not valid, it should throw
     * <code>Validator.InvalidValueException</code>
     * 
     * @param value
     *            the value to check
     * @throws Validator.InvalidValueException
     *             if the value is not valid
     */
    public void validate(Object value) throws Validator.InvalidValueException;

    /**
     * Tests if the given value is valid.
     * 
     * @param value
     *            the value to check
     * @return <code>true</code> for valid value, otherwise <code>false</code>.
     */
    public boolean isValid(Object value);

    /**
     * Invalid value exception can be thrown by {@link Validator} when a given
     * value is not valid.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    @SuppressWarnings("serial")
    public class InvalidValueException extends RuntimeException implements
            ErrorMessage {

        /** Array of validation errors that are causing the problem. */
        private InvalidValueException[] causes = null;

        /**
         * Constructs a new <code>InvalidValueException</code> with the
         * specified detail message.
         * 
         * @param message
         *            The detail message of the problem.
         */
        public InvalidValueException(String message) {
            this(message, new InvalidValueException[] {});
        }

        /**
         * Constructs a new <code>InvalidValueException</code> with a set of
         * causing validation exceptions. The error message contains first the
         * given message and then a list of validation errors in the given
         * validatables.
         * 
         * @param message
         *            The detail message of the problem.
         * @param causes
         *            Array of validatables whos invalidities are possiblity
         *            causing the invalidity.
         */
        public InvalidValueException(String message,
                InvalidValueException[] causes) {
            super(message);
            if (causes == null) {
                throw new NullPointerException(
                        "Possible causes array must not be null");
            }

            this.causes = causes;
        }

        /**
         * See if the error message doesn't paint anything visible.
         * 
         * @return True iff the paint method does not paint anything visible.
         */
        public boolean isInvisible() {
            String msg = getMessage();
            if (msg != null && msg.length() > 0) {
                return false;
            }
            if (causes != null) {
                for (int i = 0; i < causes.length; i++) {
                    if (!causes[i].isInvisible()) {
                        return false;
                    }
                }
            }
            return true;
        }

        public final int getErrorLevel() {
            return ErrorMessage.ERROR;
        }

        public void paint(PaintTarget target) throws PaintException {
            target.startTag("error");
            target.addAttribute("level", "error");

            // Error message
            final String message = getLocalizedMessage();
            if (message != null) {
                target.addText(message);
            }

            // Paint all the causes
            for (int i = 0; i < causes.length; i++) {
                causes[i].paint(target);
            }

            target.endTag("error");
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

    @SuppressWarnings("serial")
    public class EmptyValueException extends Validator.InvalidValueException {

        public EmptyValueException(String message) {
            super(message);
        }

    }
}
