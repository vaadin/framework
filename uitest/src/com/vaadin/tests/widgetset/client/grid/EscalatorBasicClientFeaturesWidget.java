package com.vaadin.tests.widgetset.client.grid;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.widget.escalator.EscalatorUpdater;
import com.vaadin.client.widget.escalator.FlyweightCell;
import com.vaadin.client.widget.escalator.Row;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.escalator.RowContainer.BodyRowContainer;
import com.vaadin.client.widget.escalator.Spacer;
import com.vaadin.client.widget.escalator.SpacerUpdater;
import com.vaadin.client.widgets.Escalator;
import com.vaadin.shared.ui.grid.ScrollDestination;

public class EscalatorBasicClientFeaturesWidget extends
        PureGWTTestApplication<Escalator> {

    public static class LogWidget extends Composite {

        private static final int MAX_LOG = 9;

        private final HTML html = new HTML();
        private final List<String> logs = new ArrayList<String>();
        private Escalator escalator;

        public LogWidget() {
            initWidget(html);
            getElement().setId("log");
        }

        public void setEscalator(Escalator escalator) {
            this.escalator = escalator;
        }

        public void updateDebugLabel() {
            int headers = escalator.getHeader().getRowCount();
            int bodys = escalator.getBody().getRowCount();
            int footers = escalator.getFooter().getRowCount();
            int columns = escalator.getColumnConfiguration().getColumnCount();

            while (logs.size() > MAX_LOG) {
                logs.remove(0);
            }

            String logString = "<hr>";
            for (String log : logs) {
                logString += log + "<br>";
            }

            html.setHTML("Columns: " + columns + "<br>" + //
                    "Header rows: " + headers + "<br>" + //
                    "Body rows: " + bodys + "<br>" + //
                    "Footer rows: " + footers + "<br>" + //
                    logString);
        }

        public void log(String string) {
            logs.add((Duration.currentTimeMillis() % 10000) + ": " + string);
        }
    }

    public static class UpdaterLifetimeWidget extends
            EscalatorBasicClientFeaturesWidget {

        private final EscalatorUpdater debugUpdater = new EscalatorUpdater() {
            @Override
            public void preAttach(Row row, Iterable<FlyweightCell> cellsToAttach) {
                log("preAttach", cellsToAttach);
            }

            @Override
            public void postAttach(Row row,
                    Iterable<FlyweightCell> attachedCells) {
                log("postAttach", attachedCells);
            }

            @Override
            public void update(Row row, Iterable<FlyweightCell> cellsToUpdate) {
                log("update", cellsToUpdate);
            }

            @Override
            public void preDetach(Row row, Iterable<FlyweightCell> cellsToDetach) {
                log("preDetach", cellsToDetach);
            }

            @Override
            public void postDetach(Row row,
                    Iterable<FlyweightCell> detachedCells) {
                log("postDetach", detachedCells);
            }

            private void log(String methodName, Iterable<FlyweightCell> cells) {
                if (!cells.iterator().hasNext()) {
                    return;
                }

                TableCellElement cellElement = cells.iterator().next()
                        .getElement();
                boolean isAttached = cellElement.getParentElement() != null
                        && cellElement.getParentElement().getParentElement() != null;
                logWidget.log(methodName + ": elementIsAttached == "
                        + isAttached);
            }
        };

        public UpdaterLifetimeWidget() {
            super();
            escalator.getHeader().setEscalatorUpdater(debugUpdater);
            escalator.getBody().setEscalatorUpdater(debugUpdater);
            escalator.getFooter().setEscalatorUpdater(debugUpdater);
        }
    }

    private static final String COLUMNS_AND_ROWS_MENU = "Columns and Rows";
    private static final String GENERAL_MENU = "General";
    private static final String FEATURES_MENU = "Features";

    private static abstract class TestEscalatorUpdater implements
            EscalatorUpdater {

        @Override
        public void preAttach(Row row, Iterable<FlyweightCell> cellsToAttach) {
            // noop
        }

        @Override
        public void postAttach(Row row, Iterable<FlyweightCell> attachedCells) {
            // noop
        }

        @Override
        public void preDetach(Row row, Iterable<FlyweightCell> cellsToDetach) {
            // noop
        }

        @Override
        public void postDetach(Row row, Iterable<FlyweightCell> detachedCells) {
            // noop
        }
    }

    private class Data {
        private int columnCounter = 0;
        private int rowCounter = 0;
        private final List<Integer> columns = new ArrayList<Integer>();
        private final List<Integer> rows = new ArrayList<Integer>();

        @SuppressWarnings("boxing")
        public void insertRows(final int offset, final int amount) {
            final List<Integer> newRows = new ArrayList<Integer>();
            for (int i = 0; i < amount; i++) {
                newRows.add(rowCounter++);
            }
            rows.addAll(offset, newRows);
        }

        @SuppressWarnings("boxing")
        public void insertColumns(final int offset, final int amount) {
            final List<Integer> newColumns = new ArrayList<Integer>();
            for (int i = 0; i < amount; i++) {
                newColumns.add(columnCounter++);
            }
            columns.addAll(offset, newColumns);
        }

        public EscalatorUpdater createHeaderUpdater() {
            return new TestEscalatorUpdater() {
                @Override
                public void update(final Row row,
                        final Iterable<FlyweightCell> cellsToUpdate) {
                    for (final FlyweightCell cell : cellsToUpdate) {
                        final Integer columnName = columns
                                .get(cell.getColumn());
                        cell.getElement().setInnerText("Header " + columnName);

                        if (colspan == Colspan.NORMAL) {
                            if (cell.getColumn() % 2 == 0) {
                                cell.setColSpan(2);
                            }
                        } else if (colspan == Colspan.CRAZY) {
                            if (cell.getColumn() % 3 == 0) {
                                cell.setColSpan(2);
                            }
                        }
                    }
                }
            };
        }

        public EscalatorUpdater createFooterUpdater() {
            return new TestEscalatorUpdater() {
                @Override
                public void update(final Row row,
                        final Iterable<FlyweightCell> cellsToUpdate) {
                    for (final FlyweightCell cell : cellsToUpdate) {
                        final Integer columnName = columns
                                .get(cell.getColumn());
                        cell.getElement().setInnerText("Footer " + columnName);

                        if (colspan == Colspan.NORMAL) {
                            if (cell.getColumn() % 2 == 0) {
                                cell.setColSpan(2);
                            }
                        } else if (colspan == Colspan.CRAZY) {
                            if (cell.getColumn() % 3 == 1) {
                                cell.setColSpan(2);
                            }
                        }
                    }
                }
            };
        }

        public EscalatorUpdater createBodyUpdater() {
            return new TestEscalatorUpdater() {

                public void renderCell(final FlyweightCell cell) {
                    final Integer columnName = columns.get(cell.getColumn());
                    final Integer rowName = rows.get(cell.getRow());
                    String cellInfo = columnName + "," + rowName;

                    if (cell.getColumn() > 0) {
                        cell.getElement().setInnerText("Cell: " + cellInfo);
                    } else {
                        cell.getElement().setInnerText(
                                "Row " + cell.getRow() + ": " + cellInfo);
                    }

                    if (colspan == Colspan.NORMAL) {
                        if (cell.getColumn() % 2 == 0) {
                            cell.setColSpan(2);
                        }
                    } else if (colspan == Colspan.CRAZY) {
                        if (cell.getColumn() % 3 == cell.getRow() % 3) {
                            cell.setColSpan(2);
                        }
                    }
                }

                @Override
                public void update(final Row row,
                        final Iterable<FlyweightCell> cellsToUpdate) {
                    for (final FlyweightCell cell : cellsToUpdate) {
                        renderCell(cell);
                    }
                }
            };
        }

        public void removeRows(final int offset, final int amount) {
            for (int i = 0; i < amount; i++) {
                rows.remove(offset);
            }
        }

        public void removeColumns(final int offset, final int amount) {
            for (int i = 0; i < amount; i++) {
                columns.remove(offset);
            }
        }
    }

    protected final Escalator escalator;
    private final Data data = new Data();

    private enum Colspan {
        NONE, NORMAL, CRAZY;
    }

    private Colspan colspan = Colspan.NONE;
    protected final LogWidget logWidget = new LogWidget();

    public EscalatorBasicClientFeaturesWidget() {
        super(new EscalatorProxy());
        escalator = getTestedWidget();
        logWidget.setEscalator(escalator);

        ((EscalatorProxy) escalator).setLogWidget(logWidget);
        addNorth(logWidget, 200);

        final RowContainer header = escalator.getHeader();
        header.setEscalatorUpdater(data.createHeaderUpdater());

        final RowContainer footer = escalator.getFooter();
        footer.setEscalatorUpdater(data.createFooterUpdater());

        escalator.getBody().setEscalatorUpdater(data.createBodyUpdater());

        setWidth("500px");
        setHeight("500px");

        escalator.getElement().getStyle().setZIndex(0);
        addNorth(escalator, 500);

        createGeneralMenu();
        createColumnMenu();
        createHeaderRowsMenu();
        createBodyRowsMenu();
        createFooterRowsMenu();
        createColumnsAndRowsMenu();
        createFrozenMenu();
        createColspanMenu();
        createSpacerMenu();
    }

    private void createFrozenMenu() {
        String[] menupath = { FEATURES_MENU, "Frozen columns" };
        addMenuCommand("Freeze 1 column", new ScheduledCommand() {
            @Override
            public void execute() {
                escalator.getColumnConfiguration().setFrozenColumnCount(1);
            }
        }, menupath);
        addMenuCommand("Freeze 0 columns", new ScheduledCommand() {
            @Override
            public void execute() {
                escalator.getColumnConfiguration().setFrozenColumnCount(0);
            }
        }, menupath);
    }

    private void createColspanMenu() {
        String[] menupath = { FEATURES_MENU, "Column spanning" };
        addMenuCommand("Apply normal colspan", new ScheduledCommand() {
            @Override
            public void execute() {
                colspan = Colspan.NORMAL;
                refreshEscalator();
            }
        }, menupath);
        addMenuCommand("Apply crazy colspan", new ScheduledCommand() {
            @Override
            public void execute() {
                colspan = Colspan.CRAZY;
                refreshEscalator();
            }
        }, menupath);
        addMenuCommand("Apply no colspan", new ScheduledCommand() {
            @Override
            public void execute() {
                colspan = Colspan.NONE;
                refreshEscalator();
            }
        }, menupath);
    }

    private void createColumnsAndRowsMenu() {
        String[] menupath = { COLUMNS_AND_ROWS_MENU };
        addMenuCommand("Add one of each row", new ScheduledCommand() {
            @Override
            public void execute() {
                insertRows(escalator.getHeader(), 0, 1);
                insertRows(escalator.getBody(), 0, 1);
                insertRows(escalator.getFooter(), 0, 1);
            }
        }, menupath);
        addMenuCommand("Remove one of each row", new ScheduledCommand() {
            @Override
            public void execute() {
                removeRows(escalator.getHeader(), 0, 1);
                removeRows(escalator.getBody(), 0, 1);
                removeRows(escalator.getFooter(), 0, 1);
            }
        }, menupath);
    }

    private void createGeneralMenu() {
        String[] menupath = { GENERAL_MENU };

        addMenuCommand("Detach Escalator", new ScheduledCommand() {
            @Override
            public void execute() {
                escalator.removeFromParent();
            }
        }, menupath);

        addMenuCommand("Attach Escalator", new ScheduledCommand() {
            @Override
            public void execute() {
                if (!escalator.isAttached()) {
                    addNorth(escalator, 500);
                }
            }
        }, menupath);

        addMenuCommand("Clear (columns, then rows)", new ScheduledCommand() {
            @Override
            public void execute() {
                resetColRow();
            }
        }, menupath);
        addMenuCommand("Clear (rows, then columns)", new ScheduledCommand() {
            @Override
            public void execute() {
                resetRowCol();
            }
        }, menupath);
        addMenuCommand("Populate Escalator (columns, then rows)",
                new ScheduledCommand() {
                    @Override
                    public void execute() {
                        resetColRow();
                        insertColumns(0, 10);
                        insertRows(escalator.getHeader(), 0, 1);
                        insertRows(escalator.getBody(), 0, 100);
                        insertRows(escalator.getFooter(), 0, 1);
                    }
                }, menupath);
        addMenuCommand("Populate Escalator (rows, then columns)",
                new ScheduledCommand() {
                    @Override
                    public void execute() {
                        resetColRow();
                        insertRows(escalator.getHeader(), 0, 1);
                        insertRows(escalator.getBody(), 0, 100);
                        insertRows(escalator.getFooter(), 0, 1);
                        insertColumns(0, 10);
                    }
                }, menupath);

        createSizeMenu();
    }

    private void createSizeMenu() {
        String[] menupath = new String[] { "General", "Size" };

        addSizeMenuItem(null, "height", menupath);
        addSizeMenuItem("200px", "height", menupath);
        addSizeMenuItem("400px", "height", menupath);
        addSizeMenuItem(null, "width", menupath);
        addSizeMenuItem("200px", "width", menupath);
        addSizeMenuItem("400px", "width", menupath);
    }

    private void addSizeMenuItem(final String size, final String direction,
            String[] menupath) {
        final String title = (size != null ? size : "undefined");
        addMenuCommand(title + " " + direction, new ScheduledCommand() {
            @Override
            public void execute() {
                if (direction.equals("height")) {
                    escalator.setHeight(size);
                } else {
                    escalator.setWidth(size);
                }
            }
        }, menupath);
    }

    private void createColumnMenu() {
        String[] menupath = { COLUMNS_AND_ROWS_MENU, "Columns" };
        addMenuCommand("Add one column to beginning", new ScheduledCommand() {
            @Override
            public void execute() {
                insertColumns(0, 1);
            }
        }, menupath);
        addMenuCommand("Add one column to end", new ScheduledCommand() {
            @Override
            public void execute() {
                insertColumns(escalator.getColumnConfiguration()
                        .getColumnCount(), 1);
            }
        }, menupath);
        addMenuCommand("Add ten columns", new ScheduledCommand() {
            @Override
            public void execute() {
                insertColumns(0, 10);
            }
        }, menupath);
        addMenuCommand("Remove one column from beginning",
                new ScheduledCommand() {
                    @Override
                    public void execute() {
                        removeColumns(0, 1);
                    }
                }, menupath);
        addMenuCommand("Remove one column from end", new ScheduledCommand() {
            @Override
            public void execute() {
                removeColumns(escalator.getColumnConfiguration()
                        .getColumnCount() - 1, 1);
            }
        }, menupath);

        addMenuCommand("Refresh first column", new ScheduledCommand() {
            @Override
            public void execute() {
                escalator.getColumnConfiguration().refreshColumns(0, 1);
            }
        }, menupath);

        addMenuCommand("Resize first column to max width",
                new ScheduledCommand() {
                    @Override
                    public void execute() {
                        escalator.getColumnConfiguration()
                                .setColumnWidth(0, -1);
                    }
                }, menupath);

        addMenuCommand("Resize first column to 100 px", new ScheduledCommand() {
            @Override
            public void execute() {
                escalator.getColumnConfiguration().setColumnWidth(0, 100);
            }
        }, menupath);
    }

    private void createHeaderRowsMenu() {
        String[] menupath = { COLUMNS_AND_ROWS_MENU, "Header Rows" };
        createRowsMenu(escalator.getHeader(), menupath);
    }

    private void createFooterRowsMenu() {
        String[] menupath = { COLUMNS_AND_ROWS_MENU, "Footer Rows" };
        createRowsMenu(escalator.getFooter(), menupath);
    }

    private void createBodyRowsMenu() {
        String[] menupath = { COLUMNS_AND_ROWS_MENU, "Body Rows" };
        createRowsMenu(escalator.getBody(), menupath);

        addMenuCommand("Add 5 rows to top", new ScheduledCommand() {
            @Override
            public void execute() {
                insertRows(escalator.getBody(), 0, 5);
            }
        }, menupath);
        addMenuCommand("Add 50 rows to top", new ScheduledCommand() {
            @Override
            public void execute() {
                insertRows(escalator.getBody(), 0, 50);
            }
        }, menupath);
        addMenuCommand("Remove 5 rows from bottom", new ScheduledCommand() {
            @Override
            public void execute() {
                removeRows(escalator.getBody(), escalator.getBody()
                        .getRowCount() - 5, 5);
            }
        }, menupath);
        addMenuCommand("Remove 50 rows from bottom", new ScheduledCommand() {
            @Override
            public void execute() {
                removeRows(escalator.getBody(), escalator.getBody()
                        .getRowCount() - 50, 50);
            }
        }, menupath);
        addMenuCommand("Remove 50 rows from almost bottom",
                new ScheduledCommand() {
                    @Override
                    public void execute() {
                        removeRows(escalator.getBody(), escalator.getBody()
                                .getRowCount() - 60, 50);
                    }
                }, menupath);
        addMenuCommand("Remove all, insert 30 and scroll 40px",
                new ScheduledCommand() {
                    @Override
                    public void execute() {
                        removeRows(escalator.getBody(), 0, escalator.getBody()
                                .getRowCount());
                        insertRows(escalator.getBody(), 0, 30);
                        escalator.setScrollTop(40);
                    }
                }, menupath);

        String[] scrollToRowMenuPath = new String[menupath.length + 1];
        System.arraycopy(menupath, 0, scrollToRowMenuPath, 0, menupath.length);
        scrollToRowMenuPath[scrollToRowMenuPath.length - 1] = "Scroll to...";
        for (int i = 0; i < 100; i += 25) {
            final int rowIndex = i;
            addMenuCommand("Row " + i, new ScheduledCommand() {
                @Override
                public void execute() {
                    escalator.scrollToRow(rowIndex, ScrollDestination.ANY, 0);
                }
            }, scrollToRowMenuPath);
        }

        addMenuCommand("Set 20px default height", new ScheduledCommand() {
            @Override
            public void execute() {
                escalator.getBody().setDefaultRowHeight(20);
            }
        }, menupath);
    }

    private void createRowsMenu(final RowContainer container, String[] menupath) {
        addMenuCommand("Add one row to beginning", new ScheduledCommand() {
            @Override
            public void execute() {
                int offset = 0;
                int number = 1;
                insertRows(container, offset, number);
            }
        }, menupath);
        addMenuCommand("Add one row to end", new ScheduledCommand() {
            @Override
            public void execute() {
                int offset = container.getRowCount();
                int number = 1;
                insertRows(container, offset, number);
            }
        }, menupath);
        addMenuCommand("Remove one row from beginning", new ScheduledCommand() {
            @Override
            public void execute() {
                int offset = 0;
                int number = 1;
                removeRows(container, offset, number);
            }
        }, menupath);
        addMenuCommand("Remove one row from end", new ScheduledCommand() {
            @Override
            public void execute() {
                int offset = container.getRowCount() - 1;
                int number = 1;
                removeRows(container, offset, number);
            }
        }, menupath);
        addMenuCommand("Remove all rows", new ScheduledCommand() {
            @Override
            public void execute() {
                if (container.getRowCount() > 0) {
                    removeRows(container, 0, container.getRowCount());
                }
            }
        }, menupath);
    }

    private void createSpacerMenu() {
        String[] menupath = { "Features", "Spacers" };

        addMenuCommand("Swap Spacer Updater", new ScheduledCommand() {
            private final SpacerUpdater CUSTOM = new SpacerUpdater() {
                @Override
                public void destroy(Spacer spacer) {
                    spacer.getElement().setInnerText("");
                }

                @Override
                public void init(Spacer spacer) {
                    spacer.getElement().setInnerText(
                            "Spacer for row " + spacer.getRow());
                }
            };

            @Override
            public void execute() {
                BodyRowContainer body = escalator.getBody();

                if (SpacerUpdater.NULL.equals(body.getSpacerUpdater())) {
                    body.setSpacerUpdater(CUSTOM);
                } else {
                    body.setSpacerUpdater(SpacerUpdater.NULL);
                }
            }
        }, menupath);

        addMenuCommand("Focusable Updater", new ScheduledCommand() {
            @Override
            public void execute() {
                escalator.getBody().setSpacerUpdater(new SpacerUpdater() {
                    @Override
                    public void init(Spacer spacer) {
                        spacer.getElement().appendChild(DOM.createInputText());
                    }

                    @Override
                    public void destroy(Spacer spacer) {
                        spacer.getElement().removeAllChildren();
                    }
                });
            }
        }, menupath);

        createSpacersMenuForRow(-1, menupath);
        createSpacersMenuForRow(1, menupath);
        createSpacersMenuForRow(50, menupath);
        createSpacersMenuForRow(99, menupath);
    }

    private void createSpacersMenuForRow(final int rowIndex, String[] menupath) {
        menupath = new String[] { menupath[0], menupath[1], "Row " + rowIndex };
        addMenuCommand("Set 100px", new ScheduledCommand() {
            @Override
            public void execute() {
                escalator.getBody().setSpacer(rowIndex, 100);
            }
        }, menupath);
        addMenuCommand("Set 50px", new ScheduledCommand() {
            @Override
            public void execute() {
                escalator.getBody().setSpacer(rowIndex, 50);
            }
        }, menupath);
        addMenuCommand("Remove", new ScheduledCommand() {
            @Override
            public void execute() {
                escalator.getBody().setSpacer(rowIndex, -1);
            }
        }, menupath);
        addMenuCommand("Scroll here (ANY, 0)", new ScheduledCommand() {
            @Override
            public void execute() {
                escalator.scrollToSpacer(rowIndex, ScrollDestination.ANY, 0);
            }
        }, menupath);
        addMenuCommand("Scroll here row+spacer below (ANY, 0)",
                new ScheduledCommand() {
                    @Override
                    public void execute() {
                        escalator.scrollToRowAndSpacer(rowIndex,
                                ScrollDestination.ANY, 0);
                    }
                }, menupath);
    }

    private void insertRows(final RowContainer container, int offset, int number) {
        if (container == escalator.getBody()) {
            data.insertRows(offset, number);
            escalator.getBody().insertRows(offset, number);
        } else {
            container.insertRows(offset, number);
        }
    }

    private void removeRows(final RowContainer container, int offset, int number) {
        if (container == escalator.getBody()) {
            data.removeRows(offset, number);
            escalator.getBody().removeRows(offset, number);
        } else {
            container.removeRows(offset, number);
        }
    }

    private void insertColumns(final int offset, final int number) {
        data.insertColumns(offset, number);
        escalator.getColumnConfiguration().insertColumns(offset, number);
    }

    private void removeColumns(final int offset, final int number) {
        data.removeColumns(offset, number);
        escalator.getColumnConfiguration().removeColumns(offset, number);
    }

    private void resetColRow() {
        if (escalator.getColumnConfiguration().getColumnCount() > 0) {
            removeColumns(0, escalator.getColumnConfiguration()
                    .getColumnCount());
        }
        if (escalator.getFooter().getRowCount() > 0) {
            removeRows(escalator.getFooter(), 0, escalator.getFooter()
                    .getRowCount());
        }

        if (escalator.getBody().getRowCount() > 0) {
            removeRows(escalator.getBody(), 0, escalator.getBody()
                    .getRowCount());
        }

        if (escalator.getHeader().getRowCount() > 0) {
            removeRows(escalator.getHeader(), 0, escalator.getHeader()
                    .getRowCount());
        }
    }

    private void resetRowCol() {
        if (escalator.getFooter().getRowCount() > 0) {
            removeRows(escalator.getFooter(), 0, escalator.getFooter()
                    .getRowCount());
        }

        if (escalator.getBody().getRowCount() > 0) {
            removeRows(escalator.getBody(), 0, escalator.getBody()
                    .getRowCount());
        }

        if (escalator.getHeader().getRowCount() > 0) {
            removeRows(escalator.getHeader(), 0, escalator.getHeader()
                    .getRowCount());
        }

        if (escalator.getColumnConfiguration().getColumnCount() > 0) {
            removeColumns(0, escalator.getColumnConfiguration()
                    .getColumnCount());
        }
    }

    private void refreshEscalator() {
        if (escalator.getHeader().getRowCount() > 0) {
            escalator.getHeader().refreshRows(0,
                    escalator.getHeader().getRowCount());
        }

        if (escalator.getBody().getRowCount() > 0) {
            escalator.getBody().refreshRows(0,
                    escalator.getBody().getRowCount());
        }

        if (escalator.getFooter().getRowCount() > 0) {
            escalator.getFooter().refreshRows(0,
                    escalator.getFooter().getRowCount());
        }
    }
}
