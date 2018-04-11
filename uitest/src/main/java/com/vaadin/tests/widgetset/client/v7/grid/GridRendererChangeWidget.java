package com.vaadin.tests.widgetset.client.v7.grid;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Button;
import com.vaadin.v7.client.renderers.ButtonRenderer;
import com.vaadin.v7.client.renderers.TextRenderer;
import com.vaadin.v7.client.widget.grid.RendererCellReference;
import com.vaadin.v7.client.widget.grid.datasources.ListDataSource;
import com.vaadin.v7.client.widgets.Grid;
import com.vaadin.v7.client.widgets.Grid.Column;

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
        String[] strArr = { "foo", "bar" };
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
