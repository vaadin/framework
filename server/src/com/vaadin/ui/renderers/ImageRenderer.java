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
package com.vaadin.ui.renderers;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.communication.URLReference;

import elemental.json.JsonValue;

/**
 * A renderer for presenting images.
 * <p>
 * The image for each rendered cell is read from a Resource-typed property in
 * the data source. Only {@link ExternalResource}s and {@link ThemeResource}s
 * are currently supported.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ImageRenderer extends ClickableRenderer<Resource> {

    /**
     * Creates a new image renderer.
     */
    public ImageRenderer() {
        super(Resource.class, null);
    }

    /**
     * Creates a new image renderer and adds the given click listener to it.
     * 
     * @param listener
     *            the click listener to register
     */
    public ImageRenderer(RendererClickListener listener) {
        this();
        addClickListener(listener);
    }

    @Override
    public JsonValue encode(Resource resource) {
        if (!(resource == null || resource instanceof ExternalResource || resource instanceof ThemeResource)) {
            throw new IllegalArgumentException(
                    "ImageRenderer only supports ExternalResource and ThemeResource ("
                            + resource.getClass().getSimpleName() + " given)");
        }

        return encode(ResourceReference.create(resource, this, null),
                URLReference.class);
    }
}
