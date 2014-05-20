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
package com.vaadin.client.ui.grid.renderers;

import java.util.Collection;

import com.google.gwt.dom.client.NativeEvent;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.CellInfo;
import com.vaadin.client.ui.grid.Renderer;

/**
 * Abstract base class for renderers.
 * 
 * @author Vaadin Ltd
 */
public abstract class AbstractRenderer<T> implements Renderer<T> {

    @Override
    public void init(Cell cell) {
        // Implement if needed
    }

    @Override
    public Collection<String> getConsumedEvents() {
        return null;
    }

    @Override
    public void onBrowserEvent(CellInfo cell, NativeEvent event) {
        // Implement if needed
    }

    @Override
    public boolean onActivate() {
        return false;
    }
}
