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

import com.vaadin.event.SerializableEventListener;

/**
 * A listener for {@code SelectionEvent}.
 * <p>
 * This is a generic listener for both type of selections, single and
 * multiselect.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the type of the selected item
 *
 * @see SelectionEvent
 *
 * @since 8.0
 */
@FunctionalInterface
public interface SelectionListener<T> extends SerializableEventListener {

    /**
     * Invoked when the selection has changed by user or programmatically.
     *
     * @param event
     *            the selection event
     */
    public void selectionChange(SelectionEvent<T> event);

}
