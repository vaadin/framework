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
package com.vaadin.client.renderers;

import com.vaadin.client.widget.grid.RendererCellReference;

/**
 * A renderer for displaying an object to a string using the
 * {@link Object#toString()} method.
 * <p>
 * If the object is <code>null</code>, then it is rendered as an empty string
 * instead.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ObjectRenderer implements Renderer<Object> {
    @Override
    public void render(RendererCellReference cell, Object data) {
        String text = (data != null) ? data.toString() : "";
        cell.getElement().setInnerText(text);
    }
}
