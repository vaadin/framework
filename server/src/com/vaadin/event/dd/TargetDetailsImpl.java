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
package com.vaadin.event.dd;

import java.util.HashMap;
import java.util.Map;

/**
 * A HashMap backed implementation of {@link TargetDetails} for terminal
 * implementation and for extension.
 * 
 * @since 6.3
 * 
 */
@SuppressWarnings("serial")
public class TargetDetailsImpl implements TargetDetails {

    private HashMap<String, Object> data = new HashMap<String, Object>();
    private DropTarget dropTarget;

    protected TargetDetailsImpl(Map<String, Object> rawDropData) {
        data.putAll(rawDropData);
    }

    public TargetDetailsImpl(Map<String, Object> rawDropData,
            DropTarget dropTarget) {
        this(rawDropData);
        this.dropTarget = dropTarget;
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
