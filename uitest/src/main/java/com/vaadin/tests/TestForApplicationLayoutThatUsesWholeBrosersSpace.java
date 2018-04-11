package com.vaadin.tests;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.v7.ui.Table;

public class TestForApplicationLayoutThatUsesWholeBrosersSpace
        extends LegacyApplication {

    LegacyWindow main = new LegacyWindow("Windowing test");

    VerticalLayout rootLayout;

    VerticalSplitPanel firstLevelSplit;

    @Override
    public void init() {
        setMainWindow(main);

        rootLayout = new VerticalLayout();
        main.setContent(rootLayout);

        rootLayout.addComponent(new Label("header"));

        firstLevelSplit = new VerticalSplitPanel();

        final HorizontalSplitPanel secondSplitPanel = new HorizontalSplitPanel();
        secondSplitPanel.setFirstComponent(new Label("left"));

        final VerticalLayout topRight = new VerticalLayout();
        topRight.addComponent(new Label("topright header"));

        final Table t = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(4, 100);
        t.setSizeFull();
        topRight.addComponent(t);
        topRight.setExpandRatio(t, 1);

        topRight.addComponent(new Label("topright footer"));

        secondSplitPanel.setSecondComponent(topRight);

        final VerticalLayout el = new VerticalLayout();
        el.addComponent(new Label("B��"));

        firstLevelSplit.setFirstComponent(secondSplitPanel);
        firstLevelSplit.setSecondComponent(el);

        rootLayout.addComponent(firstLevelSplit);
        rootLayout.setExpandRatio(firstLevelSplit, 1);

        rootLayout.addComponent(new Label("footer"));

    }

}
