/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.client.ui.csslayout;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.ui.themes.BaseTheme;

/**
 * VCCSlayout is a layout which supports configuring it's children with CSS
 * selectors
 */
public class VCssLayout extends FlowPanel {

    public static final String CLASSNAME = "v-csslayout";

    /**
     * Default constructor
     */
    public VCssLayout() {
        super();
        setStyleName(BaseTheme.UI_LAYOUT);
        addStyleName(CLASSNAME);
    }

    /**
     * Add or move a child in the
     * 
     * @param child
     * @param index
     */
    void addOrMove(Widget child, int index) {
        if (child.getParent() == this) {
            int currentIndex = getWidgetIndex(child);
            if (index == currentIndex) {
                return;
            }
        }
        insert(child, index);
    }
}
