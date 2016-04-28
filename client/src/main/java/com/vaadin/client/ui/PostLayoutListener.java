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
package com.vaadin.client.ui;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.LayoutManager;

/**
 * Interface implemented by {@link ComponentConnector} implementations that want
 * to know whenever a layout phase has ended. At the end of each layout phase,
 * {@link LayoutManager} invokes the {@link #postLayout()} method for all
 * registered component connectors implementing this interface.
 * 
 * @since 7.0
 * @author Vaadin Ltd
 */
public interface PostLayoutListener {
    /**
     * Method invoked by {@link LayoutManager} to notify the connector that a
     * layout phase has ended. This method can be used to finalize internal
     * layouting, but it is not allowed to change the its own external size or
     * modify the conditions for any children.
     */
    public void postLayout();
}
