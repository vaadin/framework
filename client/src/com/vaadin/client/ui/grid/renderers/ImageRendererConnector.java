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
package com.vaadin.client.ui.grid.renderers;

import com.google.gwt.json.client.JSONValue;
import com.vaadin.client.communication.JsonDecoder;
import com.vaadin.client.metadata.TypeDataStore;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.Connect;

/**
 * A connector for {@link ImageRenderer}.
 * 
 * @since
 * @author Vaadin Ltd
 */
@Connect(com.vaadin.ui.components.grid.renderers.ImageRenderer.class)
public class ImageRendererConnector extends AbstractRendererConnector<String> {

    @Override
    public ImageRenderer getRenderer() {
        return (ImageRenderer) super.getRenderer();
    }

    @Override
    public String decode(JSONValue value) {
        return ((URLReference) JsonDecoder.decodeValue(
                TypeDataStore.getType(URLReference.class), value, null,
                getConnection())).getURL();
    }
}
