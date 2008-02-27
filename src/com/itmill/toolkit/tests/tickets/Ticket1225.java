package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.tests.TestForTablesInitialColumnWidthLogicRendering;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;

/**
 * With IE7 extra scrollbars appear in content area all though content fits
 * properly. Scrollbars will disappear if "shaking" content a bit, like
 * selecting tests in area.
 */
public class Ticket1225 extends Application {

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