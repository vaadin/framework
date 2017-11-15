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
package com.vaadin.ui.components.grid;

import java.io.Serializable;

/**
 * A handler for calculating the index of the dropped items on the drop target
 * grid.
 *
 * @author Stephan Knitelius
 * @author Vaadin Ltd
 * @since
 * @see GridDragger
 * @param <T>
 *            the bean type
 */
@FunctionalInterface
public interface DropIndexCalculator<T> extends Serializable {
    /**
     * Called when Items are dropped onto a target grid.
     *
     * @param event
     *            the GridDropEvent.
     * @return index the target index.
     */
    public int calculateDropIndex(GridDropEvent<T> event);
}
