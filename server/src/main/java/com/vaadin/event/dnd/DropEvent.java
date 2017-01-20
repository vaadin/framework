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

import java.util.Map;

import com.vaadin.ui.Component;

public class DropEvent extends Component.Event {
    private Map<String, String> data;
    private DropTargetExtension.DropEffect dropEffect;

    DropEvent(Component source) {
        super(source);
    }

    void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getData(String format) {
        return data != null ? data.get(format) : null;
    }

    void setDropEffect(String dropEffect) {
        this.dropEffect = DropTargetExtension.DropEffect.valueOf(dropEffect);
    }

    public DropTargetExtension.DropEffect getDropEffect() {
        return dropEffect;
    }
}
