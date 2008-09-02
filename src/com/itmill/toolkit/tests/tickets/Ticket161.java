package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.tests.TestForTablesInitialColumnWidthLogicRendering;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 */
public class Ticket161 extends Application {

    private Table t;

    public void init() {

        final Window mainWin = new Window("Test app to #1368");
        setMainWindow(mainWin);

        t = TestForTablesInitialColumnWidthLogicRendering.getTestTable(3, 100);
        t.setCurrentPageFirstItemIndex(50);

        mainWin.addComponent(t);

        Button b = new Button("Truncate to 20 rows");
        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {

                Container containerDataSource = t.getContainerDataSource();
                Object[] itemIds = containerDataSource.getItemIds().toArray();
                @SuppressWarnings("unused")
                int c = 0;
                for (int i = 0; i < itemIds.length; i++) {
                    if (i > 19) {
                        containerDataSource.removeItem(itemIds[i]);
                    }
                }
            }
        });

        mainWin.addComponent(b);

    }
}