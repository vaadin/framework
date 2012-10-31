package com.vaadin.tests.components.tree;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

public class TreePerformanceTest extends AbstractTestCase {

    @Override
    protected String getDescription() {
        return "Trees rendering type may become slow escpecially with FF and big tree in a deep component tree.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow();
        setMainWindow(w);

        Layout layout = null;

        for (int i = 0; i < getLayoutCount(); i++) {
            Layout newlayout = createLayout();
            // newlayout.setHeight("100%");
            if (i == 0) {
                w.setContent(newlayout);
            } else {
                layout.addComponent(newlayout);
            }
            layout = newlayout;
        }

        Tree tree = new Tree();

        for (int i = 0; i < getItemCount(); i++) {
            String text = "ITEM " + i;
            tree.addItem(text);
            for (int j = 0; j < getSubItemCount(); j++) {
                String subtext = " SUBITEM " + j + " for " + text;
                tree.addItem(subtext);
                tree.setParent(subtext, text);
            }
            tree.expandItemsRecursively(text);
        }

        // One can test that the slugginesh is actually verticallayout issue
        // Table testTable = TestForTablesInitialColumnWidthLogicRendering
        // .getTestTable(12, 60);
        // testTable.setPageLength(0);
        layout.addComponent(tree);

    }

    private Layout createLayout() {
        return new VerticalLayout();
    }

    private int getLayoutCount() {
        return 10;
    }

    private int getSubItemCount() {
        return 3;
    }

    private int getItemCount() {
        return 200;
    }
}
