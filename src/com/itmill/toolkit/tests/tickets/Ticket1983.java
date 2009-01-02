package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * Test class for ticket 1983
 */
public class Ticket1983 extends Application {

    @Override
    public void init() {
        Window main = new Window("Test for ticket 1983");
        main.setLayout(new TestLayout());
        setMainWindow(main);
    }

    private static class TestLayout extends SplitPanel {
        boolean isLong = true;
        final Table table = new MyTable();
        final String propId = "col";
        final String propId2 = "col2";

        public TestLayout() {
            super(ORIENTATION_HORIZONTAL);

            setSplitPosition(200, Sizeable.UNITS_PIXELS);
            setMargin(false);
            setLocked(true);

            final SplitPanel leftSide = initLeftSide();
            setFirstComponent(leftSide);

            final Layout rightSide = new OrderedLayout();
            rightSide.setHeight("100%");
            setSecondComponent(rightSide);
        }

        private SplitPanel initLeftSide() {
            final SplitPanel leftSide = new SplitPanel(ORIENTATION_VERTICAL);
            leftSide.setHeight("100%");

            final IndexedContainer dataSource = new IndexedContainer();
            dataSource.addContainerProperty(propId, String.class, null);
            dataSource.addContainerProperty(propId2, String.class, null);
            final Object itemId = dataSource.addItem();
            dataSource.getItem(itemId).getItemProperty(propId).setValue(
                    "Very long value that makes a scrollbar appear for sure");
            dataSource.getItem(itemId).getItemProperty(propId2).setValue(
                    "Very long value that makes a scrollbar appear for sure");

            for (int i = 0; i < 150; i++) {
                Object id = dataSource.addItem();
                dataSource
                        .getItem(id)
                        .getItemProperty(propId)
                        .setValue(
                                (i == 100 ? "Very long value that makes a scrollbar appear for sure"
                                        : "Short"));
                dataSource.getItem(id).getItemProperty(propId2).setValue(
                        "Short");
            }

            table.setSizeFull();
            table.setContainerDataSource(dataSource);
            table.setVisibleColumns(new Object[] { propId });

            leftSide.setSecondComponent(table);

            Button button = new Button("Change col value to short");
            button.addListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {
                    // Change the column value to a short one --> Should remove
                    // the scrollbar
                    if (isLong) {
                        dataSource.getItem(itemId).getItemProperty(propId)
                                .setValue("Short value");
                        dataSource.getItem(itemId).getItemProperty(propId2)
                                .setValue("Short value");
                        isLong = false;
                    } else {
                        dataSource
                                .getItem(itemId)
                                .getItemProperty(propId)
                                .setValue(
                                        "Very long value that makes a scrollbar appear for sure");
                        dataSource
                                .getItem(itemId)
                                .getItemProperty(propId2)
                                .setValue(
                                        "Very long value that makes a scrollbar appear for sure");
                        isLong = true;
                    }
                    // Works the same way with or without repaint request
                    table.requestRepaint();
                }
            });

            OrderedLayout ol = new OrderedLayout();
            ol.addComponent(button);
            leftSide.setFirstComponent(ol);

            button = new Button("Two col");
            button.addListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    Button b = event.getButton();
                    if (((Boolean) b.getValue()).booleanValue()) {
                        table
                                .setVisibleColumns(new Object[] { propId,
                                        propId2 });
                    } else {
                        table.setVisibleColumns(new Object[] { propId });
                    }

                }

            });
            button.setSwitchMode(true);
            ol.addComponent(button);

            return leftSide;
        }
    }

    static class MyTable extends Table {
        MyTable() {
            alwaysRecalculateColumnWidths = true;
        }
    }
}
