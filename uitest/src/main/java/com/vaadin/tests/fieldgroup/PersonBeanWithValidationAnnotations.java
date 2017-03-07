/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.fieldgroup;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
