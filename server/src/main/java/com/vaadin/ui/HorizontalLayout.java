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

import com.vaadin.shared.ui.orderedlayout.HorizontalLayoutState;

/**
 * Horizontal layout
 *
 * <code>HorizontalLayout</code> is a component container, which shows the
 * subcomponents in the order of their addition (horizontally).
 *
 * @author Vaadin Ltd.
 * @since 5.3
 */
@SuppressWarnings("serial")
public class HorizontalLayout extends AbstractOrderedLayout {

    /**
     * Constructs an empty HorizontalLayout.
     */
    public HorizontalLayout() {
        setSpacing(true);
    }

    /**
     * Constructs a HorizontalLayout with the given components. The components
     * are added in the given order.
     *
     * @see AbstractOrderedLayout#addComponents(Component...)
     *
     * @param children
     *            The components to add.
     */
    public HorizontalLayout(Component... children) {
        this();
        addComponents(children);
    }

    @Override
    protected HorizontalLayoutState getState() {
        return (HorizontalLayoutState) super.getState();
    }

    @Override
    protected HorizontalLayoutState getState(boolean markAsDirty) {
        return (HorizontalLayoutState) super.getState(markAsDirty);
    }

    /**
     * Expands given the components to consume all available space. The method
     * basically uses {@link #setExpandRatio(Component, float)} method and
     * configures both given components and this component so that all available
     * space is distributed to given components. The method also adds given
     * components to layout if they are not added yet.
     *
     * @param componentsToExpand
     *            the component(s) which should be expanded
     */
    public void expand(Component... componentsToExpand) {
        configureParentForExpansion();
        for (Component component : componentsToExpand) {
            if (component.getParent() != this) {
                addComponent(component);
            }
            setExpandRatio(component, 1);
            component.setWidth(100, Unit.PERCENTAGE);
        }
    }

    private void configureParentForExpansion() {
        if (getWidth() < 0) {
            // Make full width if no other size is set
            setWidth(100, Unit.PERCENTAGE);
        }
    }

}
