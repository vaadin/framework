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
package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.ButtonRenderer;
import com.vaadin.v7.ui.renderers.HtmlRenderer;
import com.vaadin.v7.ui.renderers.TextRenderer;

public class GridRendererChange extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final Grid grid = new Grid();
        grid.setColumns("num", "foo");
        grid.getColumn("num").setRenderer(new ButtonRenderer());

        for (int i = 0; i < 1000; i++) {
            grid.addRow(String.format("<b>line %d</b>", i), "" + i);
        }

        Button button = new Button("Set ButtonRenderer",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.getColumn("num").setRenderer(new ButtonRenderer());
                    }
                });

        Button buttonHtml = new Button("Set HTMLRenderer",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.getColumn("num").setRenderer(new HtmlRenderer());
                    }
                });

        Button buttonText = new Button("Set TextRenderer",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.getColumn("num").setRenderer(new TextRenderer());
                    }
                });

        addComponent(new HorizontalLayout(button, buttonHtml, buttonText));
        addComponent(grid);
    }
}