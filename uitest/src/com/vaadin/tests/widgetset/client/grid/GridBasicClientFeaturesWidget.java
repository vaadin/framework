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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.ui.VLabel;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.FlyweightCell;
import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.Grid.AbstractGridKeyEvent;
import com.vaadin.client.ui.grid.Grid.SelectionMode;
import com.vaadin.client.ui.grid.GridColumn;
import com.vaadin.client.ui.grid.GridFooter;
import com.vaadin.client.ui.grid.GridFooter.FooterRow;
import com.vaadin.client.ui.grid.GridHeader;
import com.vaadin.client.ui.grid.GridHeader.HeaderRow;
import com.vaadin.client.ui.grid.Renderer;
import com.vaadin.client.ui.grid.datasources.ListDataSource;
import com.vaadin.client.ui.grid.datasources.ListSorter;
import com.vaadin.client.ui.grid.events.BodyKeyDownHandler;
import com.vaadin.client.ui.grid.events.BodyKeyPressHandler;
import com.vaadin.client.ui.grid.events.BodyKeyUpHandler;
import com.vaadin.client.ui.grid.events.FooterKeyDownHandler;
import com.vaadin.client.ui.grid.events.FooterKeyPressHandler;
import com.vaadin.client.ui.grid.events.FooterKeyUpHandler;
import com.vaadin.client.ui.grid.events.GridKeyDownEvent;
import com.vaadin.client.ui.grid.events.GridKeyPressEvent;
import com.vaadin.client.ui.grid.events.GridKeyUpEvent;
import com.vaadin.client.ui.grid.events.HeaderKeyDownHandler;
import com.vaadin.client.ui.grid.events.HeaderKeyPressHandler;
import com.vaadin.client.ui.grid.events.HeaderKeyUpHandler;
import com.vaadin.client.ui.grid.events.ScrollEvent;
import com.vaadin.client.ui.grid.events.ScrollHandler;
import com.vaadin.client.ui.grid.renderers.DateRenderer;
import com.vaadin.client.ui.grid.renderers.HtmlRenderer;
import com.vaadin.client.ui.grid.renderers.NumberRenderer;
import com.vaadin.client.ui.grid.renderers.TextRenderer;
import com.vaadin.tests.widgetset.client.grid.GridBasicClientFeaturesWidget.Data;

/**
 * Grid basic client features test application.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridBasicClientFeaturesWidget extends
        PureGWTTestApplication<Grid<List<Data>>> {

    public static enum Renderers {
        TEXT_RENDERER, HTML_RENDERER, NUMBER_RENDERER, DATE_RENDERER;
    }

    private static final int MANUALLY_FORMATTED_COLUMNS = 5;
    public static final int COLUMNS = 12;
    public static final int ROWS = 1000;

    private final Grid<List<Data>> grid;
    private final List<List<Data>> data;
    private final ListDataSource<List<Data>> ds;
    private final ListSorter<List<Data>> sorter;

    /**
     * Our basic data object
     */
    public final static class Data {
        Object value;
    }

    /**
     * Convenience method for creating a list of Data objects to be used as a
     * Row in the data source
     * 
     * @param cols
     *            number of columns (items) to include in the row
     * @return
     */
    private List<Data> createDataRow(int cols) {
        List<Data> list = new ArrayList<Data>(cols);
        for (int i = 0; i < cols; ++i) {
            list.add(new Data());
        }
        data.add(list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public GridBasicClientFeaturesWidget() {
        super(new Grid<List<Data>>());

        // Initialize data source
        data = new ArrayList<List<Data>>();
        {
            Random rand = new Random();
            rand.setSeed(13334);
            long timestamp = 0;
            for (int row = 0; row < ROWS; row++) {

                List<Data> datarow = createDataRow(COLUMNS);
                Data d;

                int col = 0;
                for (; col < COLUMNS - MANUALLY_FORMATTED_COLUMNS; ++col) {
                    d = datarow.get(col);
                    d.value = "(" + row + ", " + col + ")";
                }

                d = datarow.get(col++);
                d.value = Integer.valueOf(row);

                d = datarow.get(col++);
                d.value = new Date(timestamp);
                timestamp += 91250000; // a bit over a day, just to get
                                       // variation

                d = datarow.get(col++);
                d.value = "<b>" + row + "</b>";

                d = datarow.get(col++);
                d.value = Integer.valueOf(rand.nextInt());

                d = datarow.get(col++);
                d.value = Integer.valueOf(rand.nextInt(5));
            }
        }

        ds = new ListDataSource<List<Data>>(data);
        grid = getTestedWidget();
        grid.getElement().setId("testComponent");
        grid.setDataSource(ds);
        grid.setSelectionMode(SelectionMode.NONE);

        sorter = new ListSorter<List<Data>>(grid);

        // Create a bunch of grid columns

        // Data source layout:
        // text (String) * (COLUMNS - MANUALLY_FORMATTED_COLUMNS + 1) |
        // rownumber (Integer) | some date (Date) | row number as HTML (String)
        // | random value (Integer)

        int col = 0;

        // Text times COLUMNS - MANUALLY_FORMATTED_COLUMNS
        for (col = 0; col < COLUMNS - MANUALLY_FORMATTED_COLUMNS; ++col) {

            final int c = col;

            GridColumn<String, List<Data>> column = new GridColumn<String, List<Data>>(
                    createRenderer(Renderers.TEXT_RENDERER)) {
                @Override
                public String getValue(List<Data> row) {
                    return (String) row.get(c).value;
                }
            };

            column.setWidth(50 + c * 25);

            grid.addColumn(column);

        }

        // Integer row number
        {
            final int c = col++;
            grid.addColumn(new GridColumn<Integer, List<Data>>(
                    createRenderer(Renderers.NUMBER_RENDERER)) {
                @Override
                public Integer getValue(List<Data> row) {
                    return (Integer) row.get(c).value;
                }
            });
        }

        // Some date
        {
            final int c = col++;
            grid.addColumn(new GridColumn<Date, List<Data>>(
                    createRenderer(Renderers.DATE_RENDERER)) {
                @Override
                public Date getValue(List<Data> row) {
                    return (Date) row.get(c).value;
                }
            });
        }

        // Row number as a HTML string
        {
            final int c = col++;
            grid.addColumn(new GridColumn<String, List<Data>>(
                    createRenderer(Renderers.HTML_RENDERER)) {
                @Override
                public String getValue(List<Data> row) {
                    return (String) row.get(c).value;
                }
            });
        }

        // Random integer value
        {
            final int c = col++;
            grid.addColumn(new GridColumn<Integer, List<Data>>(
                    createRenderer(Renderers.NUMBER_RENDERER)) {
                @Override
                public Integer getValue(List<Data> row) {
                    return (Integer) row.get(c).value;
                }
            });
        }

        // Random integer value between 0 and 5
        {
            final int c = col++;
            grid.addColumn(new GridColumn<Integer, List<Data>>(
                    createRenderer(Renderers.NUMBER_RENDERER)) {
                @Override
                public Integer getValue(List<Data> row) {
                    return (Integer) row.get(c).value;
                }
            });
        }

        setHeaderTexts(grid.getHeader().getRow(0));

        //
        // Populate the menu
        //

        createStateMenu();
        createColumnsMenu();
        createHeaderMenu();
        createFooterMenu();
        createInternalsMenu();

        grid.getElement().getStyle().setZIndex(0);
        addNorth(grid, 400);

        createKeyHandlers();
    }

    private void createInternalsMenu() {
        String[] listenersPath = { "Component", "Internals", "Listeners" };
        final Label label = new Label();
        addSouth(label, 20);

        addMenuCommand("Add scroll listener", new ScheduledCommand() {
            private HandlerRegistration scrollHandler = null;

            @Override
            public void execute() {
                if (scrollHandler != null) {
                    return;
                }
                scrollHandler = grid.addScrollHandler(new ScrollHandler() {
                    @Override
                    public void onScroll(ScrollEvent event) {
                        @SuppressWarnings("hiding")
                        final Grid<?> grid = (Grid<?>) event.getSource();
                        label.setText("scrollTop: " + grid.getScrollTop()
                                + ", scrollLeft: " + grid.getScrollLeft());
                    }
                });
            }
        }, listenersPath);
    }

    private void createStateMenu() {
        String[] selectionModePath = { "Component", "State", "Selection mode" };
        String[] primaryStyleNamePath = { "Component", "State",
                "Primary Stylename" };

        addMenuCommand("multi", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setSelectionMode(SelectionMode.MULTI);
            }
        }, selectionModePath);

        addMenuCommand("single", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setSelectionMode(SelectionMode.SINGLE);
            }
        }, selectionModePath);

        addMenuCommand("none", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setSelectionMode(SelectionMode.NONE);
            }
        }, selectionModePath);

        addMenuCommand("v-grid", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setStylePrimaryName("v-grid");

            }
        }, primaryStyleNamePath);

        addMenuCommand("v-escalator", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setStylePrimaryName("v-escalator");

            }
        }, primaryStyleNamePath);

        addMenuCommand("v-custom-style", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setStylePrimaryName("v-custom-style");

            }
        }, primaryStyleNamePath);

        addMenuCommand("Enabled", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.getEditorRow()
                        .setEnabled(!grid.getEditorRow().isEnabled());
            }
        }, "Component", "State", "Editor row");

        addMenuCommand("Edit row 5", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.getEditorRow().editRow(5);
            }
        }, "Component", "State", "Editor row");

        addMenuCommand("Edit row 100", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.getEditorRow().editRow(100);
            }
        }, "Component", "State", "Editor row");
    }

    private void createColumnsMenu() {

        for (int i = 0; i < COLUMNS; i++) {
            final int index = i;
            addMenuCommand("Visible", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getColumn(index).setVisible(
                            !grid.getColumn(index).isVisible());
                }
            }, "Component", "Columns", "Column " + i);
            addMenuCommand("Sortable", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getColumn(index).setSortable(
                            !grid.getColumn(index).isSortable());
                }
            }, "Component", "Columns", "Column " + i);

            addMenuCommand("auto", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getColumn(index).setWidth(-1);
                }
            }, "Component", "Columns", "Column " + i, "Width");
            addMenuCommand("50px", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getColumn(index).setWidth(50);
                }
            }, "Component", "Columns", "Column " + i, "Width");
            addMenuCommand("200px", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getColumn(index).setWidth(200);
                }
            }, "Component", "Columns", "Column " + i, "Width");

            // Header types
            addMenuCommand("Text Header", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getHeader().getRow(0).getCell(index)
                            .setText("Text Header");
                }
            }, "Component", "Columns", "Column " + i, "Header Type");
            addMenuCommand("HTML Header", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getHeader().getRow(0).getCell(index)
                            .setHtml("<b>HTML Header</b>");
                }
            }, "Component", "Columns", "Column " + i, "Header Type");
            addMenuCommand("Widget Header", new ScheduledCommand() {
                @Override
                public void execute() {
                    final Button button = new Button("Button Header");
                    button.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            button.setText("Clicked");
                        }
                    });
                    grid.getHeader().getRow(0).getCell(index).setWidget(button);
                }
            }, "Component", "Columns", "Column " + i, "Header Type");

            // Footer types
            addMenuCommand("Text Footer", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getFooter().getRow(0).getCell(index)
                            .setText("Text Footer");
                }
            }, "Component", "Columns", "Column " + i, "Footer Type");
            addMenuCommand("HTML Footer", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getFooter().getRow(0).getCell(index)
                            .setHtml("<b>HTML Footer</b>");
                }
            }, "Component", "Columns", "Column " + i, "Footer Type");
            addMenuCommand("Widget Footer", new ScheduledCommand() {
                @Override
                public void execute() {
                    final Button button = new Button("Button Footer");
                    button.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            button.setText("Clicked");
                        }
                    });
                    grid.getFooter().getRow(0).getCell(index).setWidget(button);
                }
            }, "Component", "Columns", "Column " + i, "Footer Type");
        }
    }

    private int headerCounter = 0;
    private int footerCounter = 0;

    private void setHeaderTexts(HeaderRow row) {
        for (int i = 0; i < COLUMNS; ++i) {
            String caption = "Header (" + headerCounter + "," + i + ")";

            // Lets use some different cell types
            if (i % 3 == 0) {
                row.getCell(i).setText(caption);
            } else if (i % 2 == 0) {
                row.getCell(i).setHtml("<b>" + caption + "</b>");
            } else {
                row.getCell(i).setWidget(new HTML(caption));
            }
        }
        headerCounter++;
    }

    private void setFooterTexts(FooterRow row) {
        for (int i = 0; i < COLUMNS; ++i) {
            String caption = "Footer (" + footerCounter + "," + i + ")";

            // Lets use some different cell types
            if (i % 3 == 0) {
                row.getCell(i).setText(caption);
            } else if (i % 2 == 0) {
                row.getCell(i).setHtml("<b>" + caption + "</b>");
            } else {
                row.getCell(i).setWidget(new HTML(caption));
            }
        }
        footerCounter++;
    }

    private void createHeaderMenu() {
        final GridHeader header = grid.getHeader();
        final String[] menuPath = { "Component", "Header" };

        addMenuCommand("Visible", new ScheduledCommand() {
            @Override
            public void execute() {
                header.setVisible(!header.isVisible());
            }
        }, menuPath);

        addMenuCommand("Top", new ScheduledCommand() {
            @Override
            public void execute() {
                header.setDefaultRow(header.getRow(0));
            }
        }, "Component", "Header", "Default row");
        addMenuCommand("Bottom", new ScheduledCommand() {
            @Override
            public void execute() {
                header.setDefaultRow(header.getRow(header.getRowCount() - 1));
            }
        }, "Component", "Header", "Default row");
        addMenuCommand("Unset", new ScheduledCommand() {
            @Override
            public void execute() {
                header.setDefaultRow(null);
            }
        }, "Component", "Header", "Default row");

        addMenuCommand("Prepend row", new ScheduledCommand() {
            @Override
            public void execute() {
                configureHeaderRow(header.prependRow());
            }
        }, menuPath);
        addMenuCommand("Append row", new ScheduledCommand() {
            @Override
            public void execute() {
                configureHeaderRow(header.appendRow());
            }
        }, menuPath);
        addMenuCommand("Remove top row", new ScheduledCommand() {
            @Override
            public void execute() {
                header.removeRow(0);
            }
        }, menuPath);
        addMenuCommand("Remove bottom row", new ScheduledCommand() {
            @Override
            public void execute() {
                header.removeRow(header.getRowCount() - 1);
            }
        }, menuPath);

    }

    private void configureHeaderRow(final HeaderRow row) {
        final GridHeader header = grid.getHeader();
        setHeaderTexts(row);
        String rowTitle = "Row " + header.getRowCount();
        final String[] menuPath = { "Component", "Header", rowTitle };

        addMenuCommand("Join column cells 0, 1", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(row.getCell(0), row.getCell(1));

            }
        }, menuPath);

        addMenuCommand("Join columns 1, 2", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(grid.getColumn(1), grid.getColumn(2));

            }
        }, menuPath);

        addMenuCommand("Join columns 3, 4, 5", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(grid.getColumn(3), grid.getColumn(4),
                        grid.getColumn(5));

            }
        }, menuPath);

        addMenuCommand("Join all columns", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(grid.getColumns().toArray(
                        new GridColumn[grid.getColumnCount()]));

            }
        }, menuPath);
    }

    private void createFooterMenu() {
        final GridFooter footer = grid.getFooter();
        final String[] menuPath = { "Component", "Footer" };

        addMenuCommand("Visible", new ScheduledCommand() {
            @Override
            public void execute() {
                footer.setVisible(!footer.isVisible());
            }
        }, menuPath);

        addMenuCommand("Prepend row", new ScheduledCommand() {
            @Override
            public void execute() {
                configureFooterRow(footer.prependRow());
            }
        }, menuPath);
        addMenuCommand("Append row", new ScheduledCommand() {
            @Override
            public void execute() {
                configureFooterRow(footer.appendRow());
            }
        }, menuPath);
        addMenuCommand("Remove top row", new ScheduledCommand() {
            @Override
            public void execute() {
                footer.removeRow(0);
            }
        }, menuPath);
        addMenuCommand("Remove bottom row", new ScheduledCommand() {
            @Override
            public void execute() {
                assert footer.getRowCount() > 0;
                footer.removeRow(footer.getRowCount() - 1);
            }
        }, menuPath);
    }

    private void configureFooterRow(final FooterRow row) {
        final GridFooter footer = grid.getFooter();
        setFooterTexts(row);
        String rowTitle = "Row " + footer.getRowCount();
        final String[] menuPath = { "Component", "Footer", rowTitle };

        addMenuCommand("Join column cells 0, 1", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(row.getCell(0), row.getCell(1));

            }
        }, menuPath);

        addMenuCommand("Join columns 1, 2", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(grid.getColumn(1), grid.getColumn(2));

            }
        }, menuPath);

        addMenuCommand("Join all columns", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(grid.getColumns().toArray(
                        new GridColumn[grid.getColumnCount()]));

            }
        }, menuPath);
    }

    /**
     * Creates a renderer for a {@link Renderers}
     */
    @SuppressWarnings("rawtypes")
    private final Renderer createRenderer(Renderers renderer) {
        switch (renderer) {
        case TEXT_RENDERER:
            return new TextRenderer();

        case HTML_RENDERER:
            return new HtmlRenderer() {

                @Override
                public void render(FlyweightCell cell, String htmlString) {
                    super.render(cell, "<b>" + htmlString + "</b>");
                }
            };

        case NUMBER_RENDERER:
            return new NumberRenderer<Integer>();

        case DATE_RENDERER:
            return new DateRenderer();

        default:
            return new TextRenderer();
        }
    }

    /**
     * Creates a collection of handlers for all the grid key events
     */
    private void createKeyHandlers() {
        final List<VLabel> labels = new ArrayList<VLabel>();
        for (int i = 0; i < 9; ++i) {
            VLabel tmp = new VLabel();
            addNorth(tmp, 20);
            labels.add(tmp);
        }

        // Key Down Events
        grid.addKeyDownHandler(new BodyKeyDownHandler() {
            private final VLabel label = labels.get(0);

            @Override
            public void onKeyDown(GridKeyDownEvent event) {
                updateLabel(label, event);
            }
        });

        grid.addKeyDownHandler(new HeaderKeyDownHandler() {
            private final VLabel label = labels.get(1);

            @Override
            public void onKeyDown(GridKeyDownEvent event) {
                updateLabel(label, event);
            }
        });

        grid.addKeyDownHandler(new FooterKeyDownHandler() {
            private final VLabel label = labels.get(2);

            @Override
            public void onKeyDown(GridKeyDownEvent event) {
                updateLabel(label, event);
            }
        });

        // Key Up Events
        grid.addKeyUpHandler(new BodyKeyUpHandler() {
            private final VLabel label = labels.get(3);

            @Override
            public void onKeyUp(GridKeyUpEvent event) {
                updateLabel(label, event);
            }
        });

        grid.addKeyUpHandler(new HeaderKeyUpHandler() {
            private final VLabel label = labels.get(4);

            @Override
            public void onKeyUp(GridKeyUpEvent event) {
                updateLabel(label, event);
            }
        });

        grid.addKeyUpHandler(new FooterKeyUpHandler() {
            private final VLabel label = labels.get(5);

            @Override
            public void onKeyUp(GridKeyUpEvent event) {
                updateLabel(label, event);
            }
        });

        // Key Press Events
        grid.addKeyPressHandler(new BodyKeyPressHandler() {
            private final VLabel label = labels.get(6);

            @Override
            public void onKeyPress(GridKeyPressEvent event) {
                updateLabel(label, event);
            }
        });

        grid.addKeyPressHandler(new HeaderKeyPressHandler() {
            private final VLabel label = labels.get(7);

            @Override
            public void onKeyPress(GridKeyPressEvent event) {
                updateLabel(label, event);
            }
        });

        grid.addKeyPressHandler(new FooterKeyPressHandler() {
            private final VLabel label = labels.get(8);

            @Override
            public void onKeyPress(GridKeyPressEvent event) {
                updateLabel(label, event);
            }
        });

    }

    private void updateLabel(VLabel label, AbstractGridKeyEvent<?> event) {
        String type = event.getNativeEvent().getType();
        Cell active = event.getActiveCell();
        String coords = "(" + active.getRow() + ", " + active.getColumn() + ")";
        String keyCode = "" + event.getNativeKeyCode();
        label.setText(coords + " " + type + " " + keyCode);
    }
}
