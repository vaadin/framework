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
package com.vaadin.ui.components.grid.renderers;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.components.grid.Grid;
import com.vaadin.ui.components.grid.Renderer;

/**
 * An abstract base class for server-side Grid renderers.
 * {@link com.vaadin.client.ui.grid.Renderer Grid renderers}. This class
 * currently extends the AbstractExtension superclass, but this fact should be
 * regarded as an implementation detail and subject to change in a future major
 * or minor Vaadin revision.
 * 
 * @param <T>
 *            the type this renderer knows how to present
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public abstract class AbstractRenderer<T> extends AbstractExtension implements
        Renderer<T> {

    private final Class<T> presentationType;

    protected AbstractRenderer(Class<T> presentationType) {
        this.presentationType = presentationType;
    }

    /**
     * This method is inherited from AbstractExtension but should never be
     * called directly with an AbstractRenderer.
     */
    @Deprecated
    @Override
    protected Class<Grid> getSupportedParentType() {
        return Grid.class;
    }

    /**
     * This method is inherited from AbstractExtension but should never be
     * called directly with an AbstractRenderer.
     */
    @Deprecated
    @Override
    protected void extend(AbstractClientConnector target) {
        super.extend(target);
    }

    @Override
    public Class<T> getPresentationType() {
        return presentationType;
    }
}
