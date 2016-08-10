package com.vaadin.tests.util;

import com.vaadin.legacy.data.validator.LegacyAbstractValidator;

public class AlwaysFailValidator extends LegacyAbstractValidator<Object> {
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
