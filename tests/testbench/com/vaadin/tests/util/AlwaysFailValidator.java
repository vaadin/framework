package com.vaadin.tests.util;

import com.vaadin.data.validator.AbstractValidator;

public class AlwaysFailValidator extends AbstractValidator {
    public AlwaysFailValidator() {
        super("Validation error");
    }

    public AlwaysFailValidator(String message) {
        super(message);
    }

    @Override
    protected boolean internalIsValid(Object value) {
        return false;
    }
}
