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
package com.vaadin.ui.components.grid;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.JsonCodec;

import elemental.json.JsonValue;

/**
 * An abstract base class for server-side Grid renderers.
 * {@link com.vaadin.client.ui.grid.Renderer Grid renderers}. This class
 * currently extends the AbstractExtension superclass, but this fact should be
 * regarded as an implementation detail and subject to change in a future major
 * or minor Vaadin revision.
 * 
 * @param <T>
 *            the type this renderer knows how to present
 * 
 * @since
 * @author Vaadin Ltd
 */
public abstract class AbstractRenderer<T> extends AbstractExtension implements
        Renderer<T> {

    private final Class<T> presentationType;

    protected AbstractRenderer(Class<T> presentationType) {
        this.presentationType = presentationType;
    }

    /**
     * This method is inherited from AbstractExtension but should never be
     * called directly with an AbstractRenderer.
     */
    @Deprecated
    @Override
    protected Class<Grid> getSupportedParentType() {
        return Grid.class;
    }

    /**
     * This method is inherited from AbstractExtension but should never be
     * called directly with an AbstractRenderer.
     */
    @Deprecated
    @Override
    protected void extend(AbstractClientConnector target) {
        super.extend(target);
    }

    @Override
    public Class<T> getPresentationType() {
        return presentationType;
    }

    @Override
    public JsonValue encode(T value) {
        return encode(value, getPresentationType());
    }

    /**
     * Encodes the given value to JSON.
     * <p>
     * This is a helper method that can be invoked by an {@link #encode(Object)
     * encode(T)} override if serializing a value of type other than
     * {@link #getPresentationType() the presentation type} is desired. For
     * instance, a {@code Renderer<Date>} could first turn a date value into a
     * formatted string and return {@code encode(dateString, String.class)}.
     * 
     * @param value
     *            the value to be encoded
     * @param type
     *            the type of the value
     * @return a JSON representation of the given value
     */
    protected <U> JsonValue encode(U value, Class<U> type) {
        return JsonCodec.encode(value, null, type,
                getUI().getConnectorTracker()).getEncodedValue();
    }

    /**
     * Gets the item id for a row key.
     * <p>
     * A key is used to identify a particular row on both a server and a client.
     * This method can be used to get the item id for the row key that the
     * client has sent.
     * 
     * @param key
     *            the row key for which to retrieve an item id
     * @return the item id corresponding to {@code key}
     */
    protected Object getItemId(String key) {
        if (getParent() instanceof Grid) {
            Grid grid = (Grid) getParent();
            return grid.getKeyMapper().getItemId(key);
        } else {
            throw new IllegalStateException(
                    "Renderers can be used only with Grid");
        }
    }
}
