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

import java.io.Serializable;
import java.util.function.Consumer;

import com.vaadin.ui.AbstractComponent;

/**
 * Handler for {@link BinderValidationStatus} changes.
 * <p>
 * {{@link Binder#setStatusHandler(BinderStatusHandler) Register} an instance of
 * this class to be able to customize validation status handling.
 * <p>
 * The default handler will show
 * {@link AbstractComponent#setComponentError(com.vaadin.server.ErrorMessage) an
 * error message} for failed field validations. For bean level validation errors
 * it will display the first error message in
 * {@link Binder#setStatusLabel(com.vaadin.ui.Label) status label}, if one has
 * been set.
 *
 * @author Vaadin Ltd
 *
 * @param <BEAN>
 *            the bean type of the binder
 *
 * @see BinderValidationStatus
 * @see Binder#validate()
 * @see ValidationStatus
 *
 * @since 8.0
 */
public interface BinderValidationStatusHandler<BEAN>
        extends Consumer<BinderValidationStatus<BEAN>>, Serializable {

}
