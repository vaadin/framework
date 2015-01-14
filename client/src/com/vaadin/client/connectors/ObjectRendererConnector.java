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
package com.vaadin.client.connectors;

import com.vaadin.client.renderers.TextRenderer;
import com.vaadin.shared.ui.Connect;

/**
 * A connector for {@link com.vaadin.ui.renderer.ObjectRenderer the server side
 * ObjectRenderer}.
 * <p>
 * This uses a {@link TextRenderer} to actually render the contents, as the
 * object is already converted into a string server-side.
 * 
 * @since
 * @author Vaadin Ltd
 */
@Connect(com.vaadin.ui.renderer.ObjectRenderer.class)
public class ObjectRendererConnector extends AbstractRendererConnector<String> {

    @Override
    public TextRenderer getRenderer() {
        return (TextRenderer) super.getRenderer();
    }
}
