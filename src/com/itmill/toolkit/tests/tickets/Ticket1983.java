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

/**
 * Test class for ticket 1983
 */
public class Ticket1983 extends Application {

    public void init() {
        Window main = new Window("Test for ticket 1983");
        main.setLayout(new TestLayout());
        setMainWindow(main);
    }

    private static class TestLayout extends SplitPanel {
        boolean isLong = true;

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
            final String propId = "col";
            dataSource.addContainerProperty(propId, String.class, null);
            final Object itemId = dataSource.addItem();
            dataSource.getItem(itemId).getItemProperty(propId).setValue(
                    "Very long value that makes a scrollbar appear for sure");

            final Table table = new Table();
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
                        isLong = false;
                    } else {
                        dataSource
                                .getItem(itemId)
                                .getItemProperty(propId)
                                .setValue(
                                        "Very long value that makes a scrollbar appear for sure");
                        isLong = true;
                    }
                    // Works the same way with or without repaint request
                    table.requestRepaint();
                }
            });
            leftSide.setFirstComponent(button);

            return leftSide;
        }
    }
}
