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

import java.util.EventObject;

import com.vaadin.ui.Grid;

/**
 * An event that is fired when a Grid editor is saved.
 * 
 * @author Vaadin Ltd
 * @since 8.0
 * 
 * @see EditorSaveListener
 * @see Editor#addSaveListener(EditorSaveListener)
 *
 * @param <T>
 *            the bean type
 */
public class EditorSaveEvent<T> extends EventObject {

    /**
     * Constructor for a editor save event.
     * 
     * @param editor
     *            the source of the event
     */
    public EditorSaveEvent(Editor<T> editor) {
        super(editor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Editor<T> getSource() {
        return (Editor<T>) super.getSource();
    }

    /**
     * Gets the editors' grid.
     * 
     * @return the editors' grid
     */
    public Grid<T> getGrid() {
        return getSource().getGrid();
    }
}
