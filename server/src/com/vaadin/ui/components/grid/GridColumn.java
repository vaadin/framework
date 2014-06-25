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

import java.io.Serializable;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ConverterUtil;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.grid.GridColumnState;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.renderers.TextRenderer;

/**
 * A column in the grid. Can be obtained by calling
 * {@link Grid#getColumn(Object propertyId)}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class GridColumn implements Serializable {

    /**
     * The state of the column shared to the client
     */
    private final GridColumnState state;

    /**
     * The grid this column is associated with
     */
    private final Grid grid;

    private Converter<?, Object> converter;

    /**
     * A check for allowing the {@link #GridColumn(Grid, GridColumnState)
     * constructor} to call {@link #setConverter(Converter)} with a
     * <code>null</code>, even if model and renderer aren't compatible.
     */
    private boolean isFirstConverterAssignment = true;

    /**
     * Internally used constructor.
     * 
     * @param grid
     *            The grid this column belongs to. Should not be null.
     * @param state
     *            the shared state of this column
     */
    GridColumn(Grid grid, GridColumnState state) {
        this.grid = grid;
        this.state = state;
        internalSetRenderer(new TextRenderer());
    }

    /**
     * Returns the serializable state of this column that is sent to the client
     * side connector.
     * 
     * @return the internal state of the column
     */
    GridColumnState getState() {
        return state;
    }

    /**
     * Returns the caption of the header. By default the header caption is the
     * property id of the column.
     * 
     * @return the text in the header
     * 
     * @throws IllegalStateException
     *             if the column no longer is attached to the grid
     */
    public String getHeaderCaption() throws IllegalStateException {
        checkColumnIsAttached();
        return state.header;
    }

    /**
     * Sets the caption of the header.
     * 
     * @param caption
     *            the text to show in the caption
     * 
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public void setHeaderCaption(String caption) throws IllegalStateException {
        checkColumnIsAttached();
        state.header = caption;
        grid.markAsDirty();
    }

    /**
     * Returns the caption of the footer. By default the captions are
     * <code>null</code>.
     * 
     * @return the text in the footer
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public String getFooterCaption() throws IllegalStateException {
        checkColumnIsAttached();
        return state.footer;
    }

    /**
     * Sets the caption of the footer.
     * 
     * @param caption
     *            the text to show in the caption
     * 
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public void setFooterCaption(String caption) throws IllegalStateException {
        checkColumnIsAttached();
        state.footer = caption;
        grid.markAsDirty();
    }

    /**
     * Returns the width (in pixels). By default a column is 100px wide.
     * 
     * @return the width in pixels of the column
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public int getWidth() throws IllegalStateException {
        checkColumnIsAttached();
        return state.width;
    }

    /**
     * Sets the width (in pixels).
     * 
     * @param pixelWidth
     *            the new pixel width of the column
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     * @throws IllegalArgumentException
     *             thrown if pixel width is less than zero
     */
    public void setWidth(int pixelWidth) throws IllegalStateException,
            IllegalArgumentException {
        checkColumnIsAttached();
        if (pixelWidth < 0) {
            throw new IllegalArgumentException(
                    "Pixel width should be greated than 0");
        }
        state.width = pixelWidth;
        grid.markAsDirty();
    }

    /**
     * Marks the column width as undefined meaning that the grid is free to
     * resize the column based on the cell contents and available space in the
     * grid.
     */
    public void setWidthUndefined() {
        checkColumnIsAttached();
        state.width = -1;
        grid.markAsDirty();
    }

    /**
     * Is this column visible in the grid. By default all columns are visible.
     * 
     * @return <code>true</code> if the column is visible
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public boolean isVisible() throws IllegalStateException {
        checkColumnIsAttached();
        return state.visible;
    }

    /**
     * Set the visibility of this column
     * 
     * @param visible
     *            is the column visible
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public void setVisible(boolean visible) throws IllegalStateException {
        checkColumnIsAttached();
        state.visible = visible;
        grid.markAsDirty();
    }

    /**
     * Checks if column is attached and throws an {@link IllegalStateException}
     * if it is not
     * 
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    protected void checkColumnIsAttached() throws IllegalStateException {
        if (grid.getColumnByColumnId(state.id) == null) {
            throw new IllegalStateException("Column no longer exists.");
        }
    }

    /**
     * Sets this column as the last frozen column in its grid.
     * 
     * @throws IllegalArgumentException
     *             if the column is no longer attached to any grid
     * @see Grid#setLastFrozenColumn(GridColumn)
     */
    public void setLastFrozenColumn() {
        checkColumnIsAttached();
        grid.setLastFrozenColumn(this);
    }

    /**
     * Sets the renderer for this column.
     * <p>
     * If a suitable converter isn't defined explicitly, the session converter
     * factory is used to find a compatible converter.
     * 
     * @param renderer
     *            the renderer to use
     * @throws IllegalArgumentException
     *             if no compatible converter could be found
     * @see VaadinSession#getConverterFactory()
     * @see ConverterUtil#getConverter(Class, Class, VaadinSession)
     * @see #setConverter(Converter)
     */
    public void setRenderer(Renderer<?> renderer) {
        if (!internalSetRenderer(renderer)) {
            throw new IllegalArgumentException(
                    "Could not find a converter for converting from the model type "
                            + getModelType()
                            + " to the renderer presentation type "
                            + renderer.getPresentationType());
        }
    }

    /**
     * Sets the renderer for this column and the converter used to convert from
     * the property value type to the renderer presentation type.
     * 
     * @param renderer
     *            the renderer to use, cannot be null
     * @param converter
     *            the converter to use
     * 
     * @throws IllegalArgumentException
     *             if the renderer is already associated with a grid column
     */
    public <T> void setRenderer(Renderer<T> renderer,
            Converter<? extends T, ?> converter) {
        if (renderer.getParent() != null) {
            throw new IllegalArgumentException(
                    "Cannot set a renderer that is already connected to a grid column");
        }

        if (getRenderer() != null) {
            grid.removeExtension(getRenderer());
        }

        grid.addRenderer(renderer);
        state.rendererConnector = renderer;
        setConverter(converter);
    }

    /**
     * Sets the converter used to convert from the property value type to the
     * renderer presentation type.
     * 
     * @param converter
     *            the converter to use, or {@code null} to not use any
     *            converters
     * @throws IllegalArgumentException
     *             if the types are not compatible
     */
    public void setConverter(Converter<?, ?> converter)
            throws IllegalArgumentException {
        Class<?> modelType = getModelType();
        if (converter != null) {
            if (!converter.getModelType().isAssignableFrom(modelType)) {
                throw new IllegalArgumentException("The converter model type "
                        + converter.getModelType()
                        + " is not compatible with the property type "
                        + modelType);

            } else if (!getRenderer().getPresentationType().isAssignableFrom(
                    converter.getPresentationType())) {
                throw new IllegalArgumentException(
                        "The converter presentation type "
                                + converter.getPresentationType()
                                + " is not compatible with the renderer presentation type "
                                + getRenderer().getPresentationType());
            }
        }

        else {
            /*
             * Since the converter is null (i.e. will be removed), we need to
             * know that the renderer and model are compatible. If not, we can't
             * allow for this to happen.
             * 
             * The constructor is allowed to call this method with null without
             * any compatibility checks, therefore we have a special case for
             * it.
             */

            Class<?> rendererPresentationType = getRenderer()
                    .getPresentationType();
            if (!isFirstConverterAssignment
                    && !rendererPresentationType.isAssignableFrom(modelType)) {
                throw new IllegalArgumentException("Cannot remove converter, "
                        + "as renderer's presentation type "
                        + rendererPresentationType.getName() + " and column's "
                        + "model " + modelType.getName() + " type aren't "
                        + "directly with each other");
            }
        }

        isFirstConverterAssignment = false;

        @SuppressWarnings("unchecked")
        Converter<?, Object> castConverter = (Converter<?, Object>) converter;
        this.converter = castConverter;
    }

    /**
     * Returns the renderer instance used by this column.
     * 
     * @return the renderer
     */
    public Renderer<?> getRenderer() {
        return (Renderer<?>) getState().rendererConnector;
    }

    /**
     * Returns the converter instance used by this column.
     * 
     * @return the converter
     */
    public Converter<?, ?> getConverter() {
        return converter;
    }

    private <T> boolean internalSetRenderer(Renderer<T> renderer) {

        Converter<? extends T, ?> converter;
        if (isCompatibleWithProperty(renderer, getConverter())) {
            // Use the existing converter (possibly none) if types compatible
            converter = (Converter<? extends T, ?>) getConverter();
        } else {
            converter = ConverterUtil.getConverter(
                    renderer.getPresentationType(), getModelType(),
                    getSession());
        }
        setRenderer(renderer, converter);
        return isCompatibleWithProperty(renderer, converter);
    }

    private VaadinSession getSession() {
        UI ui = grid.getUI();
        return ui != null ? ui.getSession() : null;
    }

    private boolean isCompatibleWithProperty(Renderer<?> renderer,
            Converter<?, ?> converter) {
        Class<?> type;
        if (converter == null) {
            type = getModelType();
        } else {
            type = converter.getPresentationType();
        }
        return renderer.getPresentationType().isAssignableFrom(type);
    }

    private Class<?> getModelType() {
        return grid.getContainerDatasource().getType(
                grid.getPropertyIdByColumnId(state.id));
    }

    /**
     * Should sorting controls be available for the column
     * 
     * @param sortable
     *            <code>true</code> if the sorting controls should be visible.
     */
    public void setSortable(boolean sortable) {
        checkColumnIsAttached();
        state.sortable = sortable;
        grid.markAsDirty();
    }

    /**
     * Are the sorting controls visible in the column header
     */
    public boolean isSortable() {
        return state.sortable;
    }
}
