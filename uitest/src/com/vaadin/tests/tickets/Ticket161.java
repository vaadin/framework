package com.vaadin.tests.tickets;

import com.vaadin.data.Container;
import com.vaadin.server.LegacyApplication;
import com.vaadin.tests.TestForTablesInitialColumnWidthLogicRendering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;

/**
 */
public class Ticket161 extends LegacyApplication {

    private Table t;

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow("Test app to #1368");
        setMainWindow(mainWin);

        t = TestForTablesInitialColumnWidthLogicRendering.getTestTable(3, 100);
        t.setCurrentPageFirstItemIndex(50);

        mainWin.addComponent(t);

        Button b = new Button("Truncate to 20 rows");
        b.addListener(new Button.ClickListener() {
            @Override
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
