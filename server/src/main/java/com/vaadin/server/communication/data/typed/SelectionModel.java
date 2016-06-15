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
package com.vaadin.server.communication.data.typed;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.vaadin.event.handler.Handler;
import com.vaadin.event.handler.Registration;
import com.vaadin.ui.components.HasValue;

/**
 * Generic selection model interface.
 * 
 * @since
 * @param <T>
 *            type of selected values
 */
public interface SelectionModel<T> extends Serializable {

    /**
     * Selection model for selection a single value.
     *
     * @param <T>
     *            type of selected values
     */
    public interface Single<T> extends SelectionModel<T>, HasValue<T> {
    }

    /**
     * Selection model for selection multiple values.
     *
     * @param <T>
     *            type of selected values
     */
    public interface Multi<T> extends SelectionModel<T>,
            HasValue<Collection<T>> {
    }

    /**
     * Get current selection.
     * 
     * @return selection
     */
    Collection<T> getSelected();

    /**
     * Dummy selection model.
     * 
     * @param <T>
     *            selected data type
     */
    public static class NullSelectionModel<T> implements
            SelectionModel.Single<T> {

        @Override
        public void setValue(T value) {
            // NO-OP
        }

        @Override
        public T getValue() {
            return null;
        }

        @Override
        public Registration onChange(Handler<T> onChange) {
            return () -> {
                // NO-OP
            };
        }

        @Override
        public List<T> getSelected() {
            return Collections.emptyList();
        }
    }
}
