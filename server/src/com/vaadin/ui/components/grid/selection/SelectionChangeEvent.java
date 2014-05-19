/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.ui.components.grid.selection;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.vaadin.ui.components.grid.Grid;

/**
 * An event that specifies what in a selection has changed, and where the
 * selection took place.
 * 
 * @since 7.4.0
 * @author Vaadin Ltd
 */
public class SelectionChangeEvent extends EventObject {

    private Set<Object> oldSelection;
    private Set<Object> newSelection;

    public SelectionChangeEvent(Grid source, Collection<Object> oldSelection,
            Collection<Object> newSelection) {
        super(source);
        this.oldSelection = new HashSet<Object>(oldSelection);
        this.newSelection = new HashSet<Object>(newSelection);
    }

    /**
     * A {@link Collection} of all the itemIds that became selected.
     * <p>
     * <em>Note:</em> this excludes all itemIds that might have been previously
     * selected.
     * 
     * @return a Collection of the itemIds that became selected
     */
    public Set<Object> getAdded() {
        return Sets.difference(newSelection, oldSelection);
    }

    /**
     * A {@link Collection} of all the itemIds that became deselected.
     * <p>
     * <em>Note:</em> this excludes all itemIds that might have been previously
     * deselected.
     * 
     * @return a Collection of the itemIds that became deselected
     */
    public Set<Object> getRemoved() {
        return Sets.difference(oldSelection, newSelection);
    }

    @Override
    public Grid getSource() {
        return (Grid) super.getSource();
    }
}
