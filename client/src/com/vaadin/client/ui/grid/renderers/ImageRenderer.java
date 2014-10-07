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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.vaadin.client.ui.grid.FlyweightCell;

/**
 * A renderer that renders an image into a cell.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ImageRenderer extends WidgetRenderer<String, Image> {

    @Override
    public Image createWidget() {
        return GWT.create(Image.class);
    }

    @Override
    public void render(FlyweightCell cell, String url, Image image) {
        image.setUrl(url);
    }
}
