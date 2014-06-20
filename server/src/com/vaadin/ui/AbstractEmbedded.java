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

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.AbstractEmbeddedState;

/**
 * Abstract base for embedding components.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 7.0
 */
@SuppressWarnings("serial")
public abstract class AbstractEmbedded extends AbstractComponent {

    @Override
    protected AbstractEmbeddedState getState() {
        return (AbstractEmbeddedState) super.getState();
    }

    @Override
    protected AbstractEmbeddedState getState(boolean markAsDirty) {
        return (AbstractEmbeddedState) super.getState(markAsDirty);
    }

    /**
     * Sets the object source resource. The dimensions are assumed if possible.
     * The type is guessed from resource.
     * 
     * @param source
     *            the source to set.
     */
    public void setSource(Resource source) {
        setResource(AbstractEmbeddedState.SOURCE_RESOURCE, source);
    }

    /**
     * Get the object source resource.
     * 
     * @return the source
     */
    public Resource getSource() {
        return getResource(AbstractEmbeddedState.SOURCE_RESOURCE);
    }

    /**
     * Sets this component's alternate text that can be presented instead of the
     * component's normal content for accessibility purposes.
     * 
     * @param altText
     *            A short, human-readable description of this component's
     *            content.
     */
    public void setAlternateText(String altText) {
        getState().alternateText = altText;
    }

    /**
     * Gets this component's alternate text that can be presented instead of the
     * component's normal content for accessibility purposes.
     * 
     * @returns Alternate text
     */
    public String getAlternateText() {
        return getState(false).alternateText;
    }

}
