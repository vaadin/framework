package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.TestForTablesInitialColumnWidthLogicRendering;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * With IE7 extra scrollbars appear in content area all though content fits
 * properly. Scrollbars will disappear if "shaking" content a bit, like
 * selecting tests in area.
 */
public class Ticket1225 extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow(
                "Test app to break layout fuction in IE7");
        setMainWindow(mainWin);

        VerticalSplitPanel sp = new VerticalSplitPanel();

        sp.setFirstComponent(new Label("First"));

        VerticalLayout el = new VerticalLayout();

        sp.setSecondComponent(el);
        el.setMargin(true);
        el.setSizeFull();

        el.addComponent(new Label("Top"));

        Table testTable = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(5, 50);
        testTable.setSizeFull();

        TabSheet ts = new TabSheet();
        ts.setSizeFull();

        Label red = new Label(
                "<div style='background:red;width:100%;height:100%;'>??</div>",
                ContentMode.HTML);
        // red.setCaption("cap");
        // red.setSizeFull();

        // el.addComponent(testTable);
        // el.setExpandRatio(testTable,1);

        el.addComponent(ts);
        el.setExpandRatio(ts, 1);
        ts.addComponent(red);
        ts.getTab(red).setCaption("REd tab");

        Label l = new Label("<div style='background:blue;'>sdf</div>",
                ContentMode.HTML);
        el.addComponent(l);
        el.setComponentAlignment(l, Alignment.MIDDLE_RIGHT);

        mainWin.setContent(sp);

    }
}
