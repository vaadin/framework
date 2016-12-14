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

import com.vaadin.server.SerializableFunction;
import com.vaadin.ui.Component;

/**
 * A callback interface for generating an editor component corresponding to an
 * editable column of a grid. The generated component will be used in the grid
 * editor to edit the value of the column for the selected grid row.
 * 
 * @author Vaadin Ltd.
 * @since 8.0
 *
 * @param <BEAN>
 *            the bean type this generator is compatible with
 */
public interface EditorComponentGenerator<BEAN>
        extends SerializableFunction<BEAN, Component> {

    /**
     * Gets a component for a given {@code bean}.
     * 
     * @param bean
     *            the bean this component will be used to edit
     * @return the generated component
     */
    @Override
    public Component apply(BEAN bean);
}
