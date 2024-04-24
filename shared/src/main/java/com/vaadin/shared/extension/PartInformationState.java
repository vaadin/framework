/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.shared.extension;

import com.vaadin.shared.communication.SharedState;

/**
 * Shared state for {@code AbstractEventTriggerExtensionConnector} extension.
 *
 * @since 8.4
 */
public class PartInformationState extends SharedState {

    /**
     * Information passed to the widget on the client side, to allow it to
     * attach to the correct DOM element.
     */
    public String partInformation;

}
