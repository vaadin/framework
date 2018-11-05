package com.vaadin.data.validator;

import java.util.Objects;

import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

/**
 * Simple validator to check against {@code null} value and empty {@link String}
 * value.
 * <p>
 * This validator can be suitable for fields that have been marked as required
 * with {@link HasValue#setRequiredIndicatorVisible(boolean)}.
 * <p>
 * Note that
 * {@link BindingBuilder#asRequired(com.vaadin.data.ErrorMessageProvider)} does
 * almost the same thing, but verifies against the value NOT being equal to what
 * {@link HasValue#getEmptyValue()} returns and sets the required indicator
 * visible with {@link HasValue#setRequiredIndicatorVisible(boolean)}.
 *
 * @see HasValue#setRequiredIndicatorVisible(boolean)
 * @see BindingBuilder#asRequired(com.vaadin.data.ErrorMessageProvider)
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public class NotEmptyValidator<T> implements Validator<T> {

    private final String message;

    /**
     * @param message
     *            error validation message
     */
    public NotEmptyValidator(String message) {
        this.message = message;
    }

    @Override
    public ValidationResult apply(T value, ValueContext context) {
        if (Objects.isNull(value) || Objects.equals(value, "")) {
            return ValidationResult.error(message);
        } else {
            return ValidationResult.ok();
        }
    }

}
