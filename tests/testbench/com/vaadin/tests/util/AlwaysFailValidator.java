package com.vaadin.tests.util;

import com.vaadin.data.validator.AbstractValidator;

public class AlwaysFailValidator extends AbstractValidator<Object> {
    public AlwaysFailValidator() {
        super("Validation error");
    }

    public AlwaysFailValidator(String message) {
        super(message);
    }

    @Override
    protected boolean isValidValue(Object value) {
        return false;
    }

    @Override
    public Class getType() {
        return Object.class;
    }
}
