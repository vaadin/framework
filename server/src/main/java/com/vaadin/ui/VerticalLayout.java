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

import com.vaadin.shared.ui.orderedlayout.VerticalLayoutState;

/**
 * Vertical layout
 *
 * <code>VerticalLayout</code> is a component container, which shows the
 * subcomponents in the order of their addition (vertically). A vertical layout
 * is by default 100% wide.
 *
 * @author Vaadin Ltd.
 * @since 5.3
 */
@SuppressWarnings("serial")
public class VerticalLayout extends AbstractOrderedLayout {

    /**
     * Constructs an empty VerticalLayout.
     */
    public VerticalLayout() {
        setWidth("100%");
        setSpacing(true);
        setMargin(true);
    }

    /**
     * Constructs a VerticalLayout with the given components. The components are
     * added in the given order.
     *
     * @see AbstractOrderedLayout#addComponents(Component...)
     *
     * @param children
     *            The components to add.
     */
    public VerticalLayout(Component... children) {
        this();
        addComponents(children);
    }

    @Override
    protected VerticalLayoutState getState() {
        return (VerticalLayoutState) super.getState();
    }

    @Override
    protected VerticalLayoutState getState(boolean markAsDirty) {
        return (VerticalLayoutState) super.getState(markAsDirty);
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
            component.setHeight(100, Unit.PERCENTAGE);
        }
    }

    private void configureParentForExpansion() {
        if (getHeight() < 0) {
            // Make full height if no other size is set
            setHeight(100, Unit.PERCENTAGE);
        }
    }

}
