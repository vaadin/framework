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

package com.vaadin.ui;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.composite.CompositeState;

/**
 * Composite allows creating new UI components by composition of existing
 * server-side components.
 * <p>
 * A composite is created by extending the Composite class and setting the
 * composition root component using {@link #setCompositionRoot(Component)}.
 * </p>
 * <p>
 * The composition root itself can contain more components. The advantage of
 * wrapping it in a composite is that the details of the composition root, such
 * as its public API, are hidden from the users of the composite.
 * </p>
 * <p>
 * A composite itself does not contribute to the DOM in any way (contrary to
 * {@link CustomComponent} which adds a {@code <div>} to the DOM.
 * </p>
 *
 * @author Vaadin Ltd.
 * @since 8.1
 */
public class Composite extends AbstractComponent implements HasComponents {

    private static final String COMPOSITE_HAS_NO_DOM_OR_WIDGET = "A composite has no DOM or widget";
    /**
     * The contained component.
     */
    private Component root = null;

    /**
     * Constructs a new empty composite.
     * <p>
     * Use {@link #setCompositionRoot(Component)} to define the contents of the
     * composite.
     */
    public Composite() {
    }

    /**
     * Constructs a new composite containing the given component.
     *
     * @param compositionRoot
     *            the root of the composition component tree. It must not be
     *            null.
     */
    public Composite(AbstractComponent compositionRoot) {
        this();
        Objects.requireNonNull(compositionRoot);
        setCompositionRoot(compositionRoot);
    }

    /**
     * Returns the composition root.
     *
     * @return the Component Composition root.
     */
    protected Component getCompositionRoot() {
        return root;
    }

    /**
     * Sets the component contained in the composite.
     * <p>
     * You must set the composition root to a non-null value before the
     * component can be used. It cannot be changed.
     * </p>
     *
     * @param compositionRoot
     *            the root of the composition component tree.
     */
    protected void setCompositionRoot(Component compositionRoot) {
        if (root != null) {
            throw new IllegalStateException(
                    "Composition root cannot be changed");
        }
        if (compositionRoot == null) {
            throw new IllegalArgumentException(
                    "Composition root cannot be null");
        }

        // set new component
        if (compositionRoot.getParent() != null) {
            // If the component already has a parent, try to remove it
            AbstractSingleComponentContainer.removeFromParent(compositionRoot);
        }

        compositionRoot.setParent(this);
        root = compositionRoot;
        markAsDirty();
    }

    /* Basic component features ------------------------------------------ */

    @Override
    public Iterator<Component> iterator() {
        if (getCompositionRoot() != null) {
            return Collections.singletonList(getCompositionRoot()).iterator();
        } else {
            return Collections.<Component> emptyList().iterator();
        }
    }

    @Override
    protected CompositeState getState() {
        return (CompositeState) super.getState();
    }

    @Override
    protected CompositeState getState(boolean markAsDirty) {
        return (CompositeState) super.getState(markAsDirty);
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        if (getCompositionRoot() == null) {
            throw new IllegalStateException(
                    "A composite must always have a composition root");
        }
    }

    @Override
    public String getStyleName() {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public void setStyleName(String style) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public void setStyleName(String style, boolean add) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public void addStyleName(String style) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public void removeStyleName(String style) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public String getPrimaryStyleName() {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public void setPrimaryStyleName(String style) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    private Component getRootOrThrow() {
        return Optional.ofNullable(getCompositionRoot())
                .orElseThrow(() -> new IllegalStateException(
                        "Composition root has not been set"));
    }

    @Override
    public float getWidth() {
        return getRootOrThrow().getWidth();
    }

    @Override
    public float getHeight() {
        return getRootOrThrow().getHeight();
    }

    @Override
    public Unit getWidthUnits() {
        return getRootOrThrow().getWidthUnits();
    }

    @Override
    public Unit getHeightUnits() {
        return getRootOrThrow().getHeightUnits();
    }

    @Override
    public void setHeight(String height) {
        getRootOrThrow().setHeight(height);
    }

    @Override
    public void setWidth(float width, Unit unit) {
        getRootOrThrow().setWidth(width, unit);
    }

    @Override
    public void setHeight(float height, Unit unit) {
        getRootOrThrow().setHeight(height, unit);
    }

    @Override
    public void setWidth(String width) {
        getRootOrThrow().setWidth(width);
    }

    @Override
    public void setSizeFull() {
        getRootOrThrow().setSizeFull();
    }

    @Override
    public void setSizeUndefined() {
        getRootOrThrow().setSizeUndefined();
    }

    @Override
    public void setWidthUndefined() {
        getRootOrThrow().setWidthUndefined();
    }

    @Override
    public void setHeightUndefined() {
        getRootOrThrow().setHeightUndefined();
    }

    @Override
    public void setId(String id) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public String getId() {
        // Design.read relies on being able to call this
        return null;
    }

    @Override
    public void setDebugId(String id) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public String getDebugId() {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public String getCaption() {
        // Design.read relies on being able to call this
        return null;
    }

    @Override
    public void setCaption(String caption) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public void setCaptionAsHtml(boolean captionAsHtml) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public boolean isCaptionAsHtml() {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public Resource getIcon() {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public void setIcon(Resource icon) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public void setDescription(String description) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public void setDescription(String description, ContentMode mode) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return null;
    }

    @Override
    public ErrorMessage getComponentError() {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

    @Override
    public void setComponentError(ErrorMessage componentError) {
        throw new UnsupportedOperationException(COMPOSITE_HAS_NO_DOM_OR_WIDGET);
    }

}
