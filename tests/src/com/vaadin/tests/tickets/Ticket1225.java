package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.tests.TestForTablesInitialColumnWidthLogicRendering;
import com.vaadin.ui.ExpandLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

/**
 * With IE7 extra scrollbars appear in content area all though content fits
 * properly. Scrollbars will disappear if "shaking" content a bit, like
 * selecting tests in area.
 */
public class Ticket1225 extends Application {

    @Override
    public void init() {

        final Window mainWin = new Window(
                "Test app to break layout fuction in IE7");
        setMainWindow(mainWin);

        SplitPanel sp = new SplitPanel();

        sp.setFirstComponent(new Label("First"));

        ExpandLayout el = new ExpandLayout();

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
                Label.CONTENT_XHTML);
        // red.setCaption("cap");
        // red.setSizeFull();

        // el.addComponent(testTable);
        // el.expand(testTable);

        el.addComponent(ts);
        el.expand(ts);
        ts.addComponent(red);
        ts.setTabCaption(red, "REd tab");

        Label l = new Label("<div style='background:blue;'>sdf</div>",
                Label.CONTENT_XHTML);
        el.addComponent(l);
        el.setComponentAlignment(l, ExpandLayout.ALIGNMENT_RIGHT,
                ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

        mainWin.setLayout(sp);

    }
}