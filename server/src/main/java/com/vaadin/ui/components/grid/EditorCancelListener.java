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

import com.vaadin.ui.Grid;

/**
 * An event listener for a {@link Grid} editor cancel events.
 * 
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @see EditorCancelEvent
 * @see Editor#addCancelListener(EditorCancelListener)
 * 
 * @param <T>
 *            the bean type
 */
@FunctionalInterface
public interface EditorCancelListener<T> extends Serializable {

    /**
     * Called when the editor is cancelled.
     * 
     * @param event
     *            cancel event
     */
    public void onEditorCancel(EditorCancelEvent<T> event);
}
