package com.vaadin.tests.components.treetable;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.CollapseListener;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.TreeTable;

public class KeepAllItemsVisible extends TestBase implements ExpandListener,
        CollapseListener {

    private static final String CAPTION = "caption";

    @Override
    protected void setup() {
        getMainWindow().getContent().setHeight(null);
        final TreeTable tt = new TreeTable();
        tt.setWidth("400px");
        tt.addContainerProperty(CAPTION, String.class, "");
        for (int i = 0; i < 20; i++) {
            String id = "Root " + i;
            Item item = tt.addItem(id);
            item.getItemProperty(CAPTION).setValue(id);
            addChildren(tt, id, 5, 2);
        }

        tt.setSelectable(true);
        tt.setImmediate(true);

        tt.addListener((ExpandListener) this);
        tt.addListener((CollapseListener) this);
        tt.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                recalculatePageLength(tt);
                tt.markAsDirty();
            }
        });
        addComponent(tt);

        recalculatePageLength(tt);

        Button b = new Button("Set pagelength to 10",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        tt.setPageLength(10);
                    }
                });
        addComponent(b);
        b = new Button("Set pagelength to 20", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tt.setPageLength(20);
            }
        });
        addComponent(b);
        b = new Button("Set pagelength to 0", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tt.setPageLength(0);
            }
        });
        addComponent(b);
    }

    private void recalculatePageLength(TreeTable tt) {
        tt.setPageLength(getVisibleNodeCount(tt));
    }

    private int getVisibleNodeCount(TreeTable tt) {
        int count = 0;
        for (Object rootId : tt.rootItemIds()) {
            count += countVisibleNodes(tt, rootId);
        }

        return count;

    }

    private int countVisibleNodes(TreeTable tt, Object node) {
        int count = 1;
        if (!tt.isCollapsed(node) && tt.hasChildren(node)) {
            for (Object childId : tt.getChildren(node)) {
                count += countVisibleNodes(tt, childId);
            }
        }

        return count;
    }

    private void addChildren(TreeTable tt, String parentId, int nr,
            int maxNesting) {
        if (maxNesting < 1) {
            return;
        }

        for (int i = 1; i <= nr; i++) {
            String id = parentId + "/" + i;
            Item item = tt.addItem(id);
            item.getItemProperty(CAPTION).setValue(id);
            tt.setParent(id, parentId);
            addChildren(tt, id, nr, maxNesting - 1);
        }

    }

    @Override
    protected String getDescription() {
        return "Keeps the TreeTable pagelength so that all expanded items are shown.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7292;
    }

    @Override
    public void nodeCollapse(CollapseEvent event) {
        recalculatePageLength((TreeTable) event.getSource());

    }

    @Override
    public void nodeExpand(ExpandEvent event) {
        recalculatePageLength((TreeTable) event.getSource());
    }

}
