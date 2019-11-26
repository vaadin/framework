/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Resource;
import com.vaadin.server.SerializableFunction;
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
        Component root = getCompositionRoot();
        return root == null ? "" : root.getStyleName();
    }

    @Override
    public void setStyleName(String style) {
        getRootOrThrow().setStyleName(style);
    }

    @Override
    public void setStyleName(String style, boolean add) {
        getRootAbstractComponentOrThrow().setStyleName(style, add);
    }

    @Override
    public void addStyleName(String style) {
        getRootOrThrow().addStyleName(style);
    }

    @Override
    public void removeStyleName(String style) {
        getRootOrThrow().removeStyleName(style);
    }

    @Override
    public String getPrimaryStyleName() {
        return getRootAbstractComponentPropertyOrNull(
                AbstractComponent::getPrimaryStyleName);
    }

    @Override
    public void setPrimaryStyleName(String style) {
        getRootOrThrow().setPrimaryStyleName(style);
    }

    private Component getRootOrThrow() {
        Component root = getCompositionRoot();
        if (root == null) {
            throw new IllegalStateException(
                    "Composition root has not been set");
        }
        return root;
    }

    private AbstractComponent getRootAbstractComponentOrThrow() {
        Component root = getRootOrThrow();
        if (!(root instanceof AbstractComponent)) {
            throw new IllegalStateException(
                    "Composition root is not AbstractComponent");
        }
        return (AbstractComponent) root;
    }

    private <T> T getRootPropertyOrNull(
            SerializableFunction<Component, T> getter) {
        Component root = getCompositionRoot();
        return root == null ? null : getter.apply(root);
    }

    private <T> T getRootAbstractComponentPropertyOrNull(
            SerializableFunction<AbstractComponent, T> getter) {
        Component root = getCompositionRoot();
        if (root instanceof AbstractComponent) {
            return getter.apply((AbstractComponent) root);
        }
        return null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        getRootOrThrow().setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return getRootOrThrow().isEnabled();
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
        getRootOrThrow().setId(id);
    }

    @Override
    public String getId() {
        return getRootPropertyOrNull(Component::getId);
    }

    @Override
    public void setDebugId(String id) {
        getRootAbstractComponentOrThrow().setDebugId(id);
    }

    @Override
    public String getDebugId() {
        return getRootAbstractComponentPropertyOrNull(
                AbstractComponent::getDebugId);
    }

    @Override
    public String getCaption() {
        return getRootPropertyOrNull(Component::getCaption);
    }

    @Override
    public void setCaption(String caption) {
        getRootOrThrow().setCaption(caption);
    }

    @Override
    public void setCaptionAsHtml(boolean captionAsHtml) {
        getRootAbstractComponentOrThrow().setCaptionAsHtml(captionAsHtml);
    }

    @Override
    public boolean isCaptionAsHtml() {
        return getRootAbstractComponentPropertyOrNull(
                AbstractComponent::isCaptionAsHtml);
    }

    @Override
    public Resource getIcon() {
        return getRootPropertyOrNull(Component::getIcon);
    }

    @Override
    public void setIcon(Resource icon) {
        getRootOrThrow().setIcon(icon);
    }

    @Override
    public String getDescription() {
        return getRootOrThrow().getDescription();
    }

    @Override
    public void setDescription(String description) {
        getRootAbstractComponentOrThrow().setDescription(description);
    }

    @Override
    public void setDescription(String description, ContentMode mode) {
        getRootAbstractComponentOrThrow().setDescription(description, mode);
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return getRootAbstractComponentPropertyOrNull(
                AbstractComponent::getErrorMessage);
    }

    @Override
    public ErrorMessage getComponentError() {
        return getRootAbstractComponentPropertyOrNull(
                AbstractComponent::getComponentError);
    }

    @Override
    public void setComponentError(ErrorMessage componentError) {
        getRootAbstractComponentOrThrow().setComponentError(componentError);
    }

}
