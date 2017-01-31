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
package com.vaadin.event.dnd;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.ui.Component;

/**
 * Server side drop event. Fired when an HTML5 drop happens. See {@link
 * DropTargetExtension#addDropListener(DropListener)}.
 */
public class DropEvent extends Component.Event {
    private Map<String, String> data;
    private DropEffect dropEffect;

    DropEvent(Component source, List<String> types, Map<String, String> data,
            DropEffect dropEffect) {
        super(source);

        // Create a linked map that preserves the order of types
        this.data = new LinkedHashMap<>();
        types.forEach(type -> this.data.put(type, data.get(type)));

        this.dropEffect = dropEffect;
    }

    /**
     * Get data from the client side {@code DataTransfer} object.
     *
     * @param format
     *         Data format, e.g. {@code text/plain} or {@code text/uri-list}.
     * @return Data for the given format if exists in the client side {@code
     * DataTransfer}, otherwise {@code null}.
     */
    public String getTransferData(String format) {
        return data != null ? data.get(format) : null;
    }

    /**
     * Get drop effect set for the current drop target.
     *
     * @return {@code dropEffect} parameter set for the current drop target.
     */
    public DropEffect getDropEffect() {
        return dropEffect;
    }
}
