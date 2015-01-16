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
package com.vaadin.client.connectors;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.JsonDecoder;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Type;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.client.metadata.TypeDataStore;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widgets.Grid.Column;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * An abstract base class for renderer connectors. A renderer connector is used
 * to link a client-side {@link Renderer} to a server-side
 * {@link com.vaadin.ui.components.grid.Renderer Renderer}. As a connector, it
 * can use the regular Vaadin RPC and shared state mechanism to pass additional
 * state and information between the client and the server. This base class
 * itself only uses the basic
 * {@link com.vaadin.shared.communication.SharedState SharedState} and no RPC
 * interfaces.
 * 
 * @param <T>
 *            the presentation type of the renderer
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public abstract class AbstractRendererConnector<T> extends
        AbstractExtensionConnector {

    private Renderer<T> renderer = null;

    private final Type presentationType = TypeDataStore
            .getPresentationType(this.getClass());

    protected AbstractRendererConnector() {
        if (presentationType == null) {
            throw new IllegalStateException(
                    "No presentation type found for "
                            + getClass().getSimpleName()
                            + ". This may be caused by some unspecified problem in widgetset compilation.");
        }
    }

    /**
     * Returns the renderer associated with this renderer connector.
     * <p>
     * A subclass of AbstractRendererConnector should override this method as
     * shown below. The framework uses
     * {@link com.google.gwt.core.client.GWT#create(Class) GWT.create(Class)} to
     * create a renderer based on the return type of the overridden method, but
     * only if {@link #createRenderer()} is not overridden as well:
     * 
     * <pre>
     * public MyRenderer getRenderer() {
     *     return (MyRenderer) super.getRenderer();
     * }
     * </pre>
     * 
     * @return the renderer bound to this connector
     */
    public Renderer<T> getRenderer() {
        if (renderer == null) {
            renderer = createRenderer();
        }
        return renderer;
    }

    /**
     * Creates a new Renderer instance associated with this renderer connector.
     * <p>
     * You should typically not override this method since the framework by
     * default generates an implementation that uses
     * {@link com.google.gwt.core.client.GWT#create(Class)} to create a renderer
     * of the same type as returned by the most specific override of
     * {@link #getRenderer()}. If you do override the method, you can't call
     * <code>super.createRenderer()</code> since the metadata needed for that
     * implementation is not generated if there's an override of the method.
     * 
     * @return a new renderer to be used with this connector
     */
    protected Renderer<T> createRenderer() {
        // TODO generate type data
        Type type = TypeData.getType(getClass());
        try {
            Type rendererType = type.getMethod("getRenderer").getReturnType();
            @SuppressWarnings("unchecked")
            Renderer<T> instance = (Renderer<T>) rendererType.createInstance();
            return instance;
        } catch (NoDataException e) {
            throw new IllegalStateException(
                    "Default implementation of createRenderer() does not work for "
                            + getClass().getSimpleName()
                            + ". This might be caused by explicitely using "
                            + "super.createRenderer() or some unspecified "
                            + "problem with the widgetset compilation.", e);
        }
    }

    /**
     * Decodes the given JSON value into a value of type T so it can be passed
     * to the {@link #getRenderer() renderer}.
     * 
     * @param value
     *            the value to decode
     * @return the decoded value of {@code value}
     */
    public T decode(JsonValue value) {
        @SuppressWarnings("unchecked")
        T decodedValue = (T) JsonDecoder.decodeValue(presentationType, value,
                null, getConnection());
        return decodedValue;
    }

    @Override
    @Deprecated
    protected void extend(ServerConnector target) {
        // NOOP
    }

    /**
     * Gets the row key for a row object.
     * <p>
     * In case this renderer wants be able to identify a row in such a way that
     * the server also understands it, the row key is used for that. Rows are
     * identified by unified keys between the client and the server.
     * 
     * @param row
     *            the row object
     * @return the row key for the given row
     */
    protected String getRowKey(JsonObject row) {
        final ServerConnector parent = getParent();
        if (parent instanceof GridConnector) {
            return ((GridConnector) parent).getRowKey(row);
        } else {
            throw new IllegalStateException("Renderers can only be used "
                    + "with a Grid.");
        }
    }

    /**
     * Gets the column id for a column.
     * <p>
     * In case this renderer wants be able to identify a column in such a way
     * that the server also understands it, the column id is used for that.
     * Columns are identified by unified ids between the client and the server.
     * 
     * @param column
     *            the column object
     * @return the column id for the given column
     */
    protected String getColumnId(Column<?, JsonObject> column) {
        final ServerConnector parent = getParent();
        if (parent instanceof GridConnector) {
            return ((GridConnector) parent).getColumnId(column);
        } else {
            throw new IllegalStateException("Renderers can only be used "
                    + "with a Grid.");
        }
    }

}
