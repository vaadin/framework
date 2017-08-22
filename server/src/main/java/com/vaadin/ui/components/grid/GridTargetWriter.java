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
import java.util.Collection;

/**
 * An event listener for a GridDragger Drop.
 * 
 * Used to write the updates to the target Grid DataProvider.
 * 
 * @author Stephan Knitelius
 * @since 8.1
 * 
 * @param <T>
 *            the bean type
 */
public interface GridTargetWriter<T> extends Serializable {
    /**
     * Called when items have been dropped on the target Grid.
     * 
     * @param index the Target index Integer.MAX when items should be added to end.
     * @param items items to be added.
     */
    public void addItems(int index, Collection<T>  items);
}
