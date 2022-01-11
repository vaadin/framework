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
package com.vaadin.event;

import java.io.Serializable;

/**
 * Marker for events which provides information of the event origin.
 *
 * @since 8.1
 */
public interface HasUserOriginated extends Serializable {
    /**
     * Returns whether this event was triggered by user interaction, on the
     * client side, or programmatically, on the server side.
     *
     * @return {@code true} if this event originates from the client,
     *         {@code false} otherwise.
     */
    public boolean isUserOriginated();

}
