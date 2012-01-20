package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.Paintable;

/**
 * Mapper between server side paintable IDs and the actual {@link Paintable}
 * objects.
 * 
 * @since 7.0
 */
public interface PaintableIdMapper {
    /**
     * Get the {@link Paintable} instance corresponding to a paintable id.
     * 
     * @param paintableId
     *            id to get
     * @return {@link Paintable} instance or null if none found
     */
    public Paintable getPaintable(String paintableId);
}
