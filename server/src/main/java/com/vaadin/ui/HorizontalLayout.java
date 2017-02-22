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
     * Adds the given components to this layout and sets them as expanded. The
     * width of all added child components are set to 100% so that the expansion
     * will be effective. The width of this layout is also set to 100% if it is
     * currently undefined.
     * <p>
     * The components are added in the provided order to the end of this layout.
     * Any components that are already children of this layout will be moved to
     * new positions.
     *
     * @param components
     *            the components to set, not <code>null</code>
     * @since 8.0
     */
    public void addComponentsAndExpand(Component... components) {
        addComponents(components);

        if (getWidth() < 0) {
            setWidth(100, Unit.PERCENTAGE);
        }

        for (Component child : components) {
            child.setWidth(100, Unit.PERCENTAGE);
            setExpandRatio(child, 1);
        }
    }

}
