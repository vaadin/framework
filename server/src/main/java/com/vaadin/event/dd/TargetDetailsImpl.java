/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.event.dd;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.dnd.event.DropEvent;

/**
 * A HashMap backed implementation of {@link TargetDetails} for terminal
 * implementation and for extension.
 *
 * @since 6.3
 * @deprecated Replaced in 8.1 by {@link DropEvent#getTransferData(String)}
 */
@Deprecated
@SuppressWarnings("serial")
public class TargetDetailsImpl implements TargetDetails {

    private Map<String, Object> data = new HashMap<>();
    private DropTarget dropTarget;

    protected TargetDetailsImpl(Map<String, Object> rawDropData) {
        data.putAll(rawDropData);
    }

    public TargetDetailsImpl(Map<String, Object> rawDropData,
            DropTarget dropTarget) {
        this(rawDropData);
        this.dropTarget = dropTarget;
    }

    /**
     * @return details about the actual event that caused the event details.
     *         Practically mouse move or mouse up.
     */
    public MouseEventDetails getMouseEvent() {
        return MouseEventDetails.deSerialize((String) getData("mouseEvent"));
    }

    @Override
    public Object getData(String key) {
        return data.get(key);
    }

    public Object setData(String key, Object value) {
        return data.put(key, value);
    }

    @Override
    public DropTarget getTarget() {
        return dropTarget;
    }

}
