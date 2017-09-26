/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.data;

/**
 * Defines the possible Severity levels that can be applied to validators.
 * 
 * @see Binder
 * @see Validator
 * @see ValidationResult
 * 
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 * @since 8.2
 */
public enum Severity {
    /** Level is irrelevant*/
    OK(BindingValidationStatus.Status.OK), 
    /** Info level - not error state.*/
    INFO(BindingValidationStatus.Status.INFO), 
    /** Warning - not error state.*/
    WARN(BindingValidationStatus.Status.WARN), 
    /** Info error, results in invalid state.*/
    ERROR(BindingValidationStatus.Status.ERROR);
    
    private final BindingValidationStatus.Status status;
    
    private Severity(BindingValidationStatus.Status status) {
        this.status = status;
    }

    public BindingValidationStatus.Status getStatus() {
        return status;
    }
}
