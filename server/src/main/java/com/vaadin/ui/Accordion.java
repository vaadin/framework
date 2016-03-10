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

import com.vaadin.shared.ui.accordion.AccordionState;

/**
 * An accordion is a component similar to a {@link TabSheet}, but with a
 * vertical orientation and the selected component presented between tabs.
 * 
 * Closable tabs are not supported by the accordion.
 * 
 * The {@link Accordion} can be styled with the .v-accordion, .v-accordion-item,
 * .v-accordion-item-first and .v-accordion-item-caption styles.
 * 
 * @see TabSheet
 */
public class Accordion extends TabSheet {
    /**
     * Creates an empty accordion.
     */
    public Accordion() {
        super();
    }

    /**
     * Constructs a new accordion containing the given components.
     * 
     * @param components
     *            The components to add to the accordion. Each component will be
     *            added to a separate tab.
     */
    public Accordion(Component... components) {
        this();
        addComponents(components);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.TabSheet#getState()
     */
    @Override
    protected AccordionState getState() {
        return (AccordionState) super.getState();
    }

}
