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
 * A horizontal split panel contains two components and lays them horizontally.
 * The first component is on the left side.
 * 
 * <pre>
 * 
 *      +---------------------++----------------------+
 *      |                     ||                      |
 *      | The first component || The second component |
 *      |                     ||                      |
 *      +---------------------++----------------------+
 *                              
 *                            ^
 *                            |
 *                      the splitter
 * 
 * </pre>
 * 
 * @author Vaadin Ltd.
 * @since 6.5
 */
public class HorizontalSplitPanel extends AbstractSplitPanel {
    /**
     * Creates an empty horizontal split panel
     */
    public HorizontalSplitPanel() {
        super();
        setSizeFull();
    }

    /**
     * Creates a horizontal split panel containing the given components
     * 
     * @param firstComponent
     *            The component to be placed to the left of the splitter
     * @param secondComponent
     *            The component to be placed to the right of the splitter
     */
    public HorizontalSplitPanel(Component firstComponent,
            Component secondComponent) {
        this();
        setFirstComponent(firstComponent);
        setSecondComponent(secondComponent);
    }
}
