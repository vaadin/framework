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

import org.jsoup.nodes.Element;

import com.vaadin.ui.declarative.DesignContext;

/**
 * Interface to be implemented by all the components that can be read from or
 * written to HTML design representation. TODO: add reference to VisualDesigner
 * 
 * @since 7.4
 * 
 * @author Vaadin Ltd
 */
public interface DesignSynchronizable extends Component {

    /**
     * Update the component state based on the given design. The component is
     * responsible not only for updating itself but also ensuring that its
     * children update their state based on the design.
     * <p>
     * This method must not modify the design.
     * 
     * @since 7.4
     * @param design
     *            The design as HTML to obtain the state from
     * @param designContext
     *            The DesignContext instance used for parsing the design
     */
    public void synchronizeFromDesign(Element design,
            DesignContext designContext);

    /**
     * Update the given design based on the component state. The component is
     * responsible not only for updating itself but also for ensuring its
     * children update themselves in the correct position in the design. The
     * caller of this method should not assume that contents of the
     * <code>design</code> parameter are presented.
     * <p>
     * This method must not modify the component state.
     * 
     * @since 7.4
     * @param design
     *            The design as HTML to update with the current state
     * @param designContext
     */
    public void synchronizeToDesign(Element design, DesignContext designContext);
}
