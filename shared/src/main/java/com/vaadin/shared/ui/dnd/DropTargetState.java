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
package com.vaadin.shared.ui.dnd;

import com.vaadin.shared.communication.SharedState;

/**
 * State class containing parameters for DropTargetExtension.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class DropTargetState extends SharedState {
    /**
     * {@code DataTransfer.dropEffect} parameter for the drag event
     */
    public DropEffect dropEffect;

    /**
     * Criteria script to allow drop event on the element
     */
    public String dropCriteria;
}
