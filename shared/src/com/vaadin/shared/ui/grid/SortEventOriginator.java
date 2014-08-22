/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.shared.ui.grid;

/**
 * Identifier for the originator of a sort event or sort order change event.
 * 
 * @since
 * @author Vaadin Ltd
 */
public enum SortEventOriginator {

    /**
     * This event was the result of an API call.
     */
    API,

    /**
     * This event was the result of a user interacting with the UI.
     */
    USER,

    /**
     * This event resulted as a side-effect of an internal event.
     */
    INTERNAL

}
