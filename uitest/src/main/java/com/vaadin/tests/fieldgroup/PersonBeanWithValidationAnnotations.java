package com.vaadin.tests.fieldgroup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class PersonBeanWithValidationAnnotations implements Serializable {
    @NotNull(message = MultipleValidationErrors.FIRST_NAME_NOT_NULL_VALIDATION_MESSAGE)
    @Size(min = 1, message = MultipleValidationErrors.FIRST_NAME_NOT_EMPTY_VALIDATION_MESSAGE)
    private String firstName;

    @NotNull(message = MultipleValidationErrors.LAST_NAME_NOT_NULL_VALIDATION_MESSAGE)
    @Size(min = 1, message = MultipleValidationErrors.LAST_NAME_NOT_EMPTY_VALIDATION_MESSAGE)
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}