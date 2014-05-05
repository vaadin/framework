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

package com.vaadin.ui;

import com.vaadin.shared.ui.MarginInfo;

/**
 * FormLayout is used by {@link Form} to layout fields. It may also be used
 * separately without {@link Form}.
 * 
 * FormLayout is a close relative of {@link VerticalLayout}, but in FormLayout
 * captions are rendered to the left of their respective components. Required
 * and validation indicators are shown between the captions and the fields.
 * 
 * FormLayout by default has component spacing on. Also margin top and margin
 * bottom are by default on.
 * 
 */
public class FormLayout extends AbstractOrderedLayout {

    public FormLayout() {
        super();
        setSpacing(true);
        setMargin(new MarginInfo(true, false, true, false));
        setWidth(100, UNITS_PERCENTAGE);
    }

    /**
     * Constructs a FormLayout and adds the given components to it.
     * 
     * @see AbstractOrderedLayout#addComponents(Component...)
     * 
     * @param children
     *            Components to add to the FormLayout
     */
    public FormLayout(Component... children) {
        this();
        addComponents(children);
    }

    /**
     * @deprecated This method currently has no effect as expand ratios are not
     *             implemented in FormLayout
     */
    @Override
    @Deprecated
    public void setExpandRatio(Component component, float ratio) {
        super.setExpandRatio(component, ratio);
    }

    /**
     * @deprecated This method currently has no effect as expand ratios are not
     *             implemented in FormLayout
     */
    @Override
    @Deprecated
    public float getExpandRatio(Component component) {
        return super.getExpandRatio(component);
    }
}
