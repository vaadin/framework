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
package com.vaadin.server;

import java.io.Serializable;

/**
 * Interface implemented by old Vaadin 7 exception types to produce the error
 * message to show in a component.
 * 
 * @since 8.0
 */
@Deprecated
public interface ErrorMessageProducer extends Serializable {

    /**
     * Gets the error message to show in the component.
     *
     * @return the error message to show
     */
    ErrorMessage getErrorMessage();

}
