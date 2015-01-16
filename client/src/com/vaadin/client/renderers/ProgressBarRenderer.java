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

import com.google.gwt.core.shared.GWT;
import com.vaadin.client.ui.VProgressBar;
import com.vaadin.client.widget.grid.RendererCellReference;

/**
 * A Renderer that represents a double value as a graphical progress bar.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ProgressBarRenderer extends WidgetRenderer<Double, VProgressBar> {

    @Override
    public VProgressBar createWidget() {
        VProgressBar progressBar = GWT.create(VProgressBar.class);
        progressBar.addStyleDependentName("static");
        return progressBar;
    }

    @Override
    public void render(RendererCellReference cell, Double data,
            VProgressBar progressBar) {
        if (data == null) {
            progressBar.setEnabled(false);
        } else {
            progressBar.setEnabled(true);
            progressBar.setState(data.floatValue());
        }
    }
}
