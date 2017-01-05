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
package com.vaadin.data;

import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.event.SerializableEventListener;
import com.vaadin.ui.AbstractComponent;

/**
 * Handler for {@link BindingValidationStatus} changes.
 * <p>
 * {@link BindingBuilder#withValidationStatusHandler(BindingValidationStatusHandler)}
 * Register} an instance of this class to be able to override the default
 * handling, which is to show
 * {@link AbstractComponent#setComponentError(com.vaadin.server.ErrorMessage) an
 * error message} for failed field validations.
 *
 * @author Vaadin Ltd
 *
 * @see BindingBuilder#withValidationStatusHandler(BindingValidationStatusHandler)
 * @see BindingValidationStatus
 *
 * @since 8.0
 *
 */
@FunctionalInterface
public interface BindingValidationStatusHandler
        extends SerializableEventListener {

    /**
     * Invoked when the validation status has changed in a binding.
     *
     * @param statusChange
     *            the changed status
     */
    public void statusChange(BindingValidationStatus<?> statusChange);
}
