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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.shared.communication.SharedState;

/**
 * State class containing parameters for DragSourceExtension.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class DragSourceState extends SharedState {

    /**
     * Event identifier for dragend event.
     */
    public static final String EVENT_DRAGEND = "dragend";

    /**
     * Event identifier for dragstart event.
     */
    public static final String EVENT_DRAGSTART = "dragstart";

    /**
     * Data type {@code "text"} for storing data in {@code DataTransfer} object.
     */
    public static final String DATA_TYPE_TEXT = "text";

    /**
     * Data type {@code "Text"}. IE 11 stores data dragged from the desktop as
     * "Text" with capital letter.
     */
    public static final String DATA_TYPE_TEXT_IE = "Text";

    /**
     * Data type {@code "text/plain"} for reading data from {@code DataTransfer}
     * object. Some browsers convert store data with {@code "text"} as {@code
     * "text/plain"} when transferring data.
     */
    public static final String DATA_TYPE_TEXT_PLAIN = "text/plain";

    public static final String RESOURCE_DRAG_IMAGE = "drag-image";

    /**
     * {@code DataTransfer.effectAllowed} parameter for the drag event.
     */
    public EffectAllowed effectAllowed = EffectAllowed.UNINITIALIZED;

    /**
     * {@code DataTransfer.types} parameter. Used to keep track of data formats
     * set for the drag event.
     */
    public List<String> types = new ArrayList<>();

    /**
     * Used to store data in the {@code DataTransfer} object for the drag event.
     */
    public Map<String, String> data = new HashMap<>();
}
