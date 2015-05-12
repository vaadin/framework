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
package com.vaadin.ui.renderers;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.Extension;

import elemental.json.JsonValue;

/**
 * A ClientConnector for controlling client-side
 * {@link com.vaadin.client.widget.grid.Renderer Grid renderers}. Renderers
 * currently extend the Extension interface, but this fact should be regarded as
 * an implementation detail and subject to change in a future major or minor
 * Vaadin revision.
 * 
 * @param <T>
 *            the type this renderer knows how to present
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface Renderer<T> extends Extension {

    /**
     * Returns the class literal corresponding to the presentation type T.
     * 
     * @return the class literal of T
     */
    Class<T> getPresentationType();

    /**
     * Encodes the given value into a {@link JsonValue}.
     * 
     * @param value
     *            the value to encode
     * @return a JSON representation of the given value
     */
    JsonValue encode(T value);

    /**
     * This method is inherited from Extension but should never be called
     * directly with a Renderer.
     */
    @Override
    @Deprecated
    void remove();

    /**
     * This method is inherited from Extension but should never be called
     * directly with a Renderer.
     */
    @Override
    @Deprecated
    void setParent(ClientConnector parent);
}
