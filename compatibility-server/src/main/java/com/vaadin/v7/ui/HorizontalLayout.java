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
package com.vaadin.v7.ui;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;

/**
 * Horizontal layout
 *
 * <code>HorizontalLayout</code> is a component container, which shows the
 * subcomponents in the order of their addition (horizontally).
 * <p>
 * This class is a compatibility version that uses the old defaults (no
 * spacing), whereas the defaults of {@link com.vaadin.ui.HorizontalLayout} have
 * changed.
 *
 * @author Vaadin Ltd.
 * @since 5.3
 *
 * @deprecated Replaced in 8.0 with {@link com.vaadin.ui.HorizontalLayout} with
 *             spacing on by default
 */
@Deprecated
@SuppressWarnings("serial")
public class HorizontalLayout extends com.vaadin.ui.HorizontalLayout {
    /**
     * Constructs an empty HorizontalLayout.
     */
    public HorizontalLayout() {
        super();
        setSpacing(false);
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

}
