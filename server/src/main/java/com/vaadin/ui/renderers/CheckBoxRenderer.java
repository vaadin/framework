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
package com.vaadin.ui.renderers;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A renderer that displays boolean valued grid columns as checkboxes.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
public class CheckBoxRenderer<T> extends ClickableRenderer<T, Boolean> {

    /**
     * Creates a new checkbox renderer, given getter and setter functions.
     * Getters and setters are used to update the object corresponding to a
     * given row in the grid on the server side.
     *
     * @param getter
     *            function for getting the desired boolean value
     * @param setter
     *            function for setting the boolean value after a change from the
     *            client side
     */
    public CheckBoxRenderer(Function<T, Boolean> getter,
            BiConsumer<T, Boolean> setter) {
        super(Boolean.class, "");
        addClickListener(clickEvent -> setter.accept(clickEvent.getItem(),
                !getter.apply(clickEvent.getItem())));
    }
}
