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
package com.vaadin.client.connectors;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.JsonDecoder;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Type;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.client.metadata.TypeDataStore;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.shared.ui.grid.renderers.AbstractRendererState;

import elemental.json.JsonValue;

/**
 * An abstract base class for renderer connectors.
 *
 * @param <T>
 *            the presentation type of the renderer
 */
public abstract class AbstractRendererConnector<T>
        extends AbstractExtensionConnector {

    private Renderer<T> renderer = null;

    private final Type presentationType = TypeDataStore
            .getPresentationType(this.getClass());

    protected AbstractRendererConnector() {
        if (presentationType == null) {
            throw new IllegalStateException("No presentation type found for "
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
                            + "problem with the widgetset compilation.",
                    e);
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

    @Override
    public AbstractRendererState getState() {
        return (AbstractRendererState) super.getState();
    }
}
