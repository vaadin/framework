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

/**
 * A vertical split panel contains two components and lays them vertically. The
 * first component is above the second component.
 * 
 * <pre>
 *      +--------------------------+
 *      |                          |
 *      |  The first component     |
 *      |                          |
 *      +==========================+  <-- splitter
 *      |                          |
 *      |  The second component    |
 *      |                          |
 *      +--------------------------+
 * </pre>
 * 
 */
public class VerticalSplitPanel extends AbstractSplitPanel {

    public VerticalSplitPanel() {
        super();
        setSizeFull();
    }

    /**
     * Creates a horizontal split panel containing the given components
     * 
     * @param firstComponent
     *            The component to be placed above the splitter
     * @param secondComponent
     *            The component to be placed below of the splitter
     */
    public VerticalSplitPanel(Component firstComponent,
            Component secondComponent) {
        this();
        setFirstComponent(firstComponent);
        setSecondComponent(secondComponent);
    }
}
