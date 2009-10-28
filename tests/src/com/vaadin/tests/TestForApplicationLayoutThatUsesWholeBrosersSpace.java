/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.Application;
import com.vaadin.ui.ExpandLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class TestForApplicationLayoutThatUsesWholeBrosersSpace extends
        Application {

    Window main = new Window("Windowing test");

    ExpandLayout rootLayout;

    SplitPanel firstLevelSplit;

    @Override
    public void init() {
        setMainWindow(main);

        rootLayout = new ExpandLayout();
        main.setLayout(rootLayout);

        rootLayout.addComponent(new Label("header"));

        firstLevelSplit = new SplitPanel();

        final SplitPanel secondSplitPanel = new SplitPanel(
                SplitPanel.ORIENTATION_HORIZONTAL);
        secondSplitPanel.setFirstComponent(new Label("left"));

        final ExpandLayout topRight = new ExpandLayout();
        topRight.addComponent(new Label("topright header"));

        final Table t = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(4, 100);
        t.setSizeFull();
        topRight.addComponent(t);
        topRight.expand(t);

        topRight.addComponent(new Label("topright footer"));

        secondSplitPanel.setSecondComponent(topRight);

        final ExpandLayout el = new ExpandLayout();
        el.addComponent(new Label("B��"));

        firstLevelSplit.setFirstComponent(secondSplitPanel);
        firstLevelSplit.setSecondComponent(el);

        rootLayout.addComponent(firstLevelSplit);
        rootLayout.expand(firstLevelSplit);

        rootLayout.addComponent(new Label("footer"));

    }

}
