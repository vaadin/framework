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
package com.vaadin.shared.ui.panel;

import com.vaadin.shared.ComponentState;

public class PanelState extends ComponentState {
    private int tabIndex;
    private int scrollLeft, scrollTop;

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public int getScrollLeft() {
        return scrollLeft;
    }

    public void setScrollLeft(int scrollLeft) {
        this.scrollLeft = scrollLeft;
    }

    public int getScrollTop() {
        return scrollTop;
    }

    public void setScrollTop(int scrollTop) {
        this.scrollTop = scrollTop;
    }

}