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
package com.vaadin.ui.components.grid;

import java.io.Serializable;

import com.vaadin.ui.Grid;

/**
 * An event listener for a {@link Grid} editor save events.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @see EditorSaveEvent
 * @see Editor#addSaveListener(EditorSaveListener)
 */
@FunctionalInterface
public interface EditorSaveListener<T> extends Serializable {

    /**
     * Called when the editor is saved.
     *
     * @param event
     *            save event
     */
    public void onEditorSave(EditorSaveEvent<T> event);
}
