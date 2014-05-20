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

package com.vaadin.tests.components.grid;

import java.util.Random;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.grid.TestGrid;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

@Widgetset(TestingWidgetSet.NAME)
public class BasicEscalator extends AbstractTestUI {
    public static final String ESCALATOR = "escalator";
    public static final String INSERT_ROWS_OFFSET = "iro";
    public static final String INSERT_ROWS_AMOUNT = "ira";
    public static final String INSERT_ROWS_BUTTON = "irb";

    private final Random random = new Random();

    @Override
    protected void setup(final VaadinRequest request) {
        final TestGrid grid = new TestGrid();
        grid.setId(ESCALATOR);
        addComponent(grid);

        final Layout insertRowsLayout = new HorizontalLayout();
        final TextField insertRowsOffset = new TextField();
        insertRowsOffset.setId(INSERT_ROWS_OFFSET);
        insertRowsLayout.addComponent(insertRowsOffset);
        final TextField insertRowsAmount = new TextField();
        insertRowsAmount.setId(INSERT_ROWS_AMOUNT);
        insertRowsLayout.addComponent(insertRowsAmount);
        insertRowsLayout.addComponent(new Button("insert rows",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        final int offset = Integer.parseInt(insertRowsOffset
                                .getValue());
                        final int amount = Integer.parseInt(insertRowsAmount
                                .getValue());
                        grid.insertRows(offset, amount);
                    }
                }) {
            {
                setId(INSERT_ROWS_BUTTON);
            }
        });
        addComponent(insertRowsLayout);

        final Layout removeRowsLayout = new HorizontalLayout();
        final TextField removeRowsOffset = new TextField();
        removeRowsLayout.addComponent(removeRowsOffset);
        final TextField removeRowsAmount = new TextField();
        removeRowsLayout.addComponent(removeRowsAmount);
        removeRowsLayout.addComponent(new Button("remove rows",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        final int offset = Integer.parseInt(removeRowsOffset
                                .getValue());
                        final int amount = Integer.parseInt(removeRowsAmount
                                .getValue());
                        grid.removeRows(offset, amount);
                    }
                }));
        addComponent(removeRowsLayout);

        final Layout insertColumnsLayout = new HorizontalLayout();
        final TextField insertColumnsOffset = new TextField();
        insertColumnsLayout.addComponent(insertColumnsOffset);
        final TextField insertColumnsAmount = new TextField();
        insertColumnsLayout.addComponent(insertColumnsAmount);
        insertColumnsLayout.addComponent(new Button("insert columns",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        final int offset = Integer.parseInt(insertColumnsOffset
                                .getValue());
                        final int amount = Integer.parseInt(insertColumnsAmount
                                .getValue());
                        grid.insertColumns(offset, amount);
                    }
                }));
        addComponent(insertColumnsLayout);

        final Layout removeColumnsLayout = new HorizontalLayout();
        final TextField removeColumnsOffset = new TextField();
        removeColumnsLayout.addComponent(removeColumnsOffset);
        final TextField removeColumnsAmount = new TextField();
        removeColumnsLayout.addComponent(removeColumnsAmount);
        removeColumnsLayout.addComponent(new Button("remove columns",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        final int offset = Integer.parseInt(removeColumnsOffset
                                .getValue());
                        final int amount = Integer.parseInt(removeColumnsAmount
                                .getValue());
                        grid.removeColumns(offset, amount);
                    }
                }));
        addComponent(removeColumnsLayout);

        final HorizontalLayout rowScroll = new HorizontalLayout();
        final NativeSelect destination = new NativeSelect();
        destination.setNullSelectionAllowed(false);
        destination.addItem("any");
        destination.setValue("any");
        destination.addItem("start");
        destination.addItem("end");
        destination.addItem("middle");
        rowScroll.addComponent(destination);
        final TextField rowIndex = new TextField();
        rowScroll.addComponent(rowIndex);
        final TextField rowPadding = new TextField();
        rowScroll.addComponent(rowPadding);
        rowScroll.addComponent(new Button("scroll to row",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        int index;
                        try {
                            index = Integer.parseInt(rowIndex.getValue());
                        } catch (NumberFormatException e) {
                            index = 0;
                        }

                        int padding;
                        try {
                            padding = Integer.parseInt(rowPadding.getValue());
                        } catch (NumberFormatException e) {
                            padding = 0;
                        }

                        grid.scrollToRow(index,
                                (String) destination.getValue(), padding);
                    }
                }));
        addComponent(rowScroll);

        final HorizontalLayout colScroll = new HorizontalLayout();
        final NativeSelect colDestination = new NativeSelect();
        colDestination.setNullSelectionAllowed(false);
        colDestination.addItem("any");
        colDestination.setValue("any");
        colDestination.addItem("start");
        colDestination.addItem("end");
        colDestination.addItem("middle");
        colScroll.addComponent(colDestination);
        final TextField colIndex = new TextField();
        colScroll.addComponent(colIndex);
        final TextField colPadding = new TextField();
        colScroll.addComponent(colPadding);
        colScroll.addComponent(new Button("scroll to column",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        int index;
                        try {
                            index = Integer.parseInt(colIndex.getValue());
                        } catch (NumberFormatException e) {
                            index = 0;
                        }

                        int padding;
                        try {
                            padding = Integer.parseInt(colPadding.getValue());
                        } catch (NumberFormatException e) {
                            padding = 0;
                        }

                        grid.scrollToColumn(index,
                                (String) colDestination.getValue(), padding);
                    }
                }));
        addComponent(colScroll);

        final TextField freezeCount = new TextField();
        freezeCount.setConverter(Integer.class);
        freezeCount.setNullRepresentation("");
        addComponent(new HorizontalLayout(freezeCount, new Button(
                "set frozen columns", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.setFrozenColumns(((Integer) freezeCount
                                .getConvertedValue()).intValue());
                        freezeCount.setValue(null);
                    }
                })));

        addComponent(new Button("Resize randomly", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                int width = random.nextInt(300) + 500;
                int height = random.nextInt(300) + 200;
                grid.setWidth(width + "px");
                grid.setHeight(height + "px");
            }
        }));

        addComponent(new Button("Random headers count",
                new Button.ClickListener() {
                    private int headers = 1;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        int diff = 0;
                        while (diff == 0) {
                            final int nextHeaders = random.nextInt(4);
                            diff = nextHeaders - headers;
                            headers = nextHeaders;
                        }
                        if (diff > 0) {
                            grid.insertHeaders(0, diff);
                        } else if (diff < 0) {
                            grid.removeHeaders(0, -diff);
                        }
                    }
                }));

        addComponent(new Button("Random footers count",
                new Button.ClickListener() {
                    private int footers = 1;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        int diff = 0;
                        while (diff == 0) {
                            final int nextFooters = random.nextInt(4);
                            diff = nextFooters - footers;
                            footers = nextFooters;
                        }
                        if (diff > 0) {
                            grid.insertFooters(0, diff);
                        } else if (diff < 0) {
                            grid.removeFooters(0, -diff);
                        }
                    }
                }));

        final Layout resizeColumnsLayout = new HorizontalLayout();
        final TextField resizeColumnIndex = new TextField();
        resizeColumnsLayout.addComponent(resizeColumnIndex);
        final TextField resizeColumnPx = new TextField();
        resizeColumnsLayout.addComponent(resizeColumnPx);
        resizeColumnsLayout.addComponent(new Button("resize column",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        final int index = Integer.parseInt(resizeColumnIndex
                                .getValue());
                        final int px = Integer.parseInt(resizeColumnPx
                                .getValue());
                        grid.setColumnWidth(index, px);
                    }
                }));
        addComponent(resizeColumnsLayout);

        addComponent(new Button("Autoresize columns",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.calculateColumnWidths();
                    }
                }));

        addComponent(new Button("Randomize row heights",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.randomizeDefaultRowHeight();
                    }
                }));
    }

    @Override
    protected String getTestDescription() {
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
