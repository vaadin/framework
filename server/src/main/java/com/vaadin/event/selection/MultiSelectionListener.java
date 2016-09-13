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

import com.vaadin.event.EventListener;

/**
 * Listens to changes from a
 * {@link com.vaadin.shared.data.selection.SelectionModel.Multi}.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 * @param <T>
 *            the data type of the selection model
 */
public interface MultiSelectionListener<T>
        extends EventListener<MultiSelectionEvent<T>> {
    @Override
    // Explicitly defined to make reflection logic happy
    void accept(MultiSelectionEvent<T> event);
}
