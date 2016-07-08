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
package com.vaadin.client.tokka.data.selection;

import elemental.json.JsonObject;

public interface SelectionModel {

    public interface Single {
    }

    public interface Multi {
    }

    /**
     * Selects the given item. Some selection models will deselect items if
     * needed.
     */
    void select(JsonObject item);

    /**
     * Deselects the given item. Some selection model can prevent this.
     */
    void deselect(JsonObject item);

    /**
     * Gets the current state of selection for given item.
     * 
     * @return {@code true} if selected; {@code false} if not
     */
    boolean isSelected(JsonObject item);
}
