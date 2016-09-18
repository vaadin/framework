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
package com.vaadin.tests.widgetset.client.grid;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Button;
import com.vaadin.client.renderers.ButtonRenderer;
import com.vaadin.client.renderers.TextRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.client.widget.grid.datasources.ListDataSource;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;

public class GridRendererChangeWidget
        extends PureGWTTestApplication<Grid<String[]>> {

    public class MyButtonRenderer extends ButtonRenderer {

        private final Button widget = new Button();

        private boolean hasInit = false;
        private boolean hasBeenDestroyed = false;
        private boolean wasAttached = false;

        @Override
        public void init(RendererCellReference cell) {
            if (hasInit || hasBeenDestroyed) {
                throw new RuntimeException("Init in an unexpected state.");
            }
            super.init(cell);

            hasInit = true;
        }

        @Override
        public Button createWidget() {
            return widget;
        }

        @Override
        public void render(RendererCellReference cell, String text,
                Button button) {
            if (!hasInit || hasBeenDestroyed) {
                throw new RuntimeException("Render in an unexpected state.");
            }
            if (button != widget) {
                throw new RuntimeException("Unexpected button instance");
            }
            if (button.getParent() != getTestedWidget()) {
                throw new RuntimeException("Button not attached!");
            }

            super.render(cell, text, button);

            wasAttached = true;
        }

        @Override
        public void destroy() {
            if (!hasInit || !wasAttached) {
                throw new RuntimeException("Destroy in an unexpected state");
            }

            super.destroy();

            hasBeenDestroyed = true;
        }

        public void verify() {
            if (!hasInit) {
                throw new RuntimeException("Failed. Not initialized");
            } else if (!wasAttached) {
                throw new RuntimeException("Failed. Not attached");
            } else if (widget.getParent() != null) {
                throw new RuntimeException("Failed. Not detached");
            } else if (!hasBeenDestroyed) {
                throw new RuntimeException("Failed. Not destroyed");
            }
        }
    }

    public GridRendererChangeWidget() {
        super(new Grid<String[]>());
        String[] strArr = new String[] { "foo", "bar" };
        ListDataSource<String[]> ds = new ListDataSource<String[]>(strArr);
        final Grid<String[]> grid = getTestedWidget();
        grid.setDataSource(ds);
        final Column<String, String[]> first = new Column<String, String[]>() {

            @Override
            public String getValue(String[] row) {
                return row[0];
            }
        };
        grid.addColumn(first).setHeaderCaption("First")
                .setRenderer(new MyButtonRenderer());
        final Column<String, String[]> second = new Column<String, String[]>() {

            @Override
            public String getValue(String[] row) {
                return row[1];
            }
        };
        grid.addColumn(second).setHeaderCaption("Second")
                .setRenderer(new MyButtonRenderer());

        addMenuCommand("Change first renderer", new ScheduledCommand() {

            boolean isButton = true;

            @Override
            public void execute() {
                if (isButton) {
                    final MyButtonRenderer r = (MyButtonRenderer) first
                            .getRenderer();
                    first.setRenderer(new TextRenderer());
                    r.verify();
                } else {
                    first.setRenderer(new MyButtonRenderer());
                }
                isButton = !isButton;
            }

        }, "Component");
        addMenuCommand("Change second renderer", new ScheduledCommand() {

            boolean isButton = true;

            @Override
            public void execute() {
                if (isButton) {
                    MyButtonRenderer r = (MyButtonRenderer) second
                            .getRenderer();
                    second.setRenderer(new TextRenderer());
                    r.verify();
                } else {
                    second.setRenderer(new MyButtonRenderer());
                }
                isButton = !isButton;
            }

        }, "Component");

        addNorth(grid, 600);

        grid.getElement().getStyle().setZIndex(0);
    }

}
