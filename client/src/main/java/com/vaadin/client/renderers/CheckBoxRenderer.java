/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import com.google.gwt.user.client.ui.CheckBox;
import com.vaadin.client.ui.VCheckBox;
import com.vaadin.client.widget.grid.RendererCellReference;

/**
 * A Renderer that displays a boolean value as a checkbox.
 *
 * <p>
 *
 * Click handlers can be added to the renderer, invoked when any of the rendered
 * checkboxes are clicked.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
public class CheckBoxRenderer extends ClickableRenderer<Boolean, CheckBox> {

    @Override
    public CheckBox createWidget() {
        CheckBox cb = GWT.create(VCheckBox.class);
        cb.addClickHandler(this);
        return cb;
    }

    @Override
    public void render(RendererCellReference cell, Boolean data,
            CheckBox widget) {
        widget.setValue(data);
    }
}
