/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.data.validator;

import com.vaadin.data.Validator;

/**
 * Abstract {@link com.vaadin.data.Validator Validator} implementation that
 * provides a basic Validator implementation except the {@link #isValid(Object)}
 * method. Sub-classes need to implement the {@link #isValid(Object)} method.
 * <p>
 * To include the value that failed validation in the exception message you can
 * use "{0}" in the error message. This will be replaced with the failed value
 * (converted to string using {@link #toString()}) or "null" if the value is
 * null.
 * </p>
 * <p>
 * The default implementation of AbstractValidator does not support HTML in
 * error messages. To enable HTML support, override
 * {@link InvalidValueException#getHtmlMessage()} and throw such exceptions from
 * {@link #validate(Object)}.
 * </p>
 * <p>
 * Since Vaadin 7, subclasses can either implement {@link #validate(Object)}
 * directly or implement {@link #internalIsValid(Object)} when migrating legacy
 * applications. To check validity, {@link #validate(Object)} should be used.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.4
 */
@SuppressWarnings("serial")
public abstract class AbstractValidator implements Validator {

    /**
     * Error message that is included in an {@link InvalidValueException} if
     * such is thrown.
     */
    private String errorMessage;

    /**
     * Constructs a validator with the given error message.
     * 
     * @param errorMessage
     *            the message to be included in an {@link InvalidValueException}
     *            (with "{0}" replaced by the value that failed validation).
     */
    public AbstractValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Since Vaadin 7, subclasses of AbstractValidator should override
     * {@link #internalIsValid(Object)} or {@link #validate(Object)} instead of
     * {@link #isValid(Object)}. {@link #validate(Object)} should be used to
     * check values.
     * 
     * This method may disappear in future Vaadin versions.
     * 
     * @param value
     * @return true if the value is valid
     * @deprecated override {@link #internalIsValid(Object)} or
     *             {@link #validate(Object)} instead of {@link #isValid(Object)}
     *             and use {@link #validate(Object)} to check value validity
     */
    @Deprecated
    protected final boolean isValid(Object value) {
        try {
            validate(value);
            return true;
        } catch (InvalidValueException e) {
            return false;
        }
    }

    /**
     * Internally check the validity of a value. This method can be overridden
     * in subclasses if customization of the error message is not needed -
     * otherwise, subclasses should override {@link #validate(Object)} instead.
     * 
     * This method should not be called from outside the validator class itself.
     * 
     * @param value
     * @return
     */
    protected boolean internalIsValid(Object value) {
        return false;
    }

    public void validate(Object value) throws InvalidValueException {
        if (!internalIsValid(value)) {
            String message = errorMessage.replace("{0}", String.valueOf(value));
            throw new InvalidValueException(message);
        }
    }

    /**
     * Returns the message to be included in the exception in case the value
     * does not validate.
     * 
     * @return the error message provided in the constructor or using
     *         {@link #setErrorMessage(String)}.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the message to be included in the exception in case the value does
     * not validate. The exception message is typically shown to the end user.
     * 
     * @param errorMessage
     *            the error message. "{0}" is automatically replaced by the
     *            value that did not validate.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
