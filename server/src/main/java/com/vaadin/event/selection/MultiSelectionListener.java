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
package com.vaadin.event.selection;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.util.ReflectTools;

/**
 * A listener for listening for selection changes from a multiselection
 * component.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 * @param <T>
 *            the type of the selected item
 *
 * @see com.vaadin.data.SelectionModel.Multi
 * @see MultiSelectionEvent
 */
@FunctionalInterface
public interface MultiSelectionListener<T> extends SerializableEventListener {

    /** For internal use only. Might be removed in the future. */
    @Deprecated
    static final Method SELECTION_CHANGE_METHOD = ReflectTools.findMethod(
            MultiSelectionListener.class, "selectionChange",
            MultiSelectionEvent.class);

    /**
     * Invoked when the selection has changed by the user or programmatically.
     *
     * @param event
     *            the selection event, never {@code null}
     */
    public void selectionChange(MultiSelectionEvent<T> event);
}
