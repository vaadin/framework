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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.vaadin.client.widget.grid.RendererCellReference;

/**
 * A renderer that renders an image into a cell. Click handlers can be added to
 * the renderer, invoked every time any of the images rendered by that rendered
 * is clicked.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ImageRenderer extends ClickableRenderer<String, Image> {

    public static final String TRANSPARENT_GIF_1PX = "data:image/gif;base64,R0lGODlhAQABAIAAAP///wAAACwAAAAAAQABAAACAkQBADs=";

    @Override
    public Image createWidget() {
        Image image = GWT.create(Image.class);
        image.addClickHandler(this);
        return image;
    }

    @Override
    public void render(RendererCellReference cell, String url, Image image) {
        if (url == null) {
            image.setUrl(TRANSPARENT_GIF_1PX);
        } else {
            image.setUrl(url);
        }
    }
}
