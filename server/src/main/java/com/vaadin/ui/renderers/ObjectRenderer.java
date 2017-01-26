package com.vaadin.ui.renderers;

import com.vaadin.server.JsonCodec;
import com.vaadin.shared.ui.grid.renderers.ObjectRendererState;

import elemental.json.JsonValue;

/**
 * A renderer for representing any type encoded as a string.
 *
 * @since 8.0
 */
public class ObjectRenderer extends AbstractRenderer<Object, Object> {

    /**
     * Creates a new object renderer, with the empty string as its
     * representation for {@code null} values.
     */
    public ObjectRenderer() {
        this("");
    }

    /**
     * Creates a new object renderer.
     *
     * @param nullRepresentation
     *            the textual representation of {@code null} value
     */
    public ObjectRenderer(String nullRepresentation) {
        super(Object.class, nullRepresentation);
    }

    @Override
    protected ObjectRendererState getState() {
        return (ObjectRendererState) super.getState();
    }

    @Override
    protected ObjectRendererState getState(boolean markAsDirty) {
        return (ObjectRendererState) super.getState(markAsDirty);
    }

    @Override
    protected <U> JsonValue encode(U value, Class<U> type) {
        return JsonCodec.encode(String.valueOf(value), null, String.class,
                getUI().getConnectorTracker()).getEncodedValue();
    }
}
