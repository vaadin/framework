package com.vaadin.tests.components.treetable;

import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TreeTable;

public class TreeTablePartialUpdates extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TreeTable tt = new TreeTable();
        tt.setContainerDataSource(makeHierarchicalContainer());
        tt.setWidth("300px");
        addComponent(tt);
    }

    @SuppressWarnings("unchecked")
    private Hierarchical makeHierarchicalContainer() {
        HierarchicalContainer hc = new HierarchicalContainer();
        hc.addContainerProperty("p1", String.class, "");
        hc.addContainerProperty("p2", String.class, "");

        Object r1 = hc.addItem();
        hc.getItem(r1).getItemProperty("p1").setValue("root1");
        hc.getItem(r1).getItemProperty("p2").setValue("root1");

        Object r2 = hc.addItem();
        hc.getItem(r2).getItemProperty("p1").setValue("root2");
        hc.getItem(r2).getItemProperty("p2").setValue("root2");

        Object r3 = hc.addItem();
        hc.getItem(r3).getItemProperty("p1").setValue("root3");
        hc.getItem(r3).getItemProperty("p2").setValue("root3");

        Object r4 = hc.addItem();
        hc.getItem(r4).getItemProperty("p1").setValue("END");
        hc.setChildrenAllowed(r4, false);

        addNodesToRoot(hc, r1, 200);
        addNodesToRoot(hc, r2, 200);
        addNodesToRoot(hc, r3, 200);
        return hc;
    }

    @SuppressWarnings("unchecked")
    private void addNodesToRoot(HierarchicalContainer hc, Object root, int count) {
        for (int ix = 0; ix < count; ix++) {
            Object id = hc.addItem();
            hc.getItem(id).getItemProperty("p1").setValue(String.valueOf(ix));
            hc.setParent(id, root);
        }
    }

    @Override
    protected String getTestDescription() {
        return "Tests partial updates and how they affect cached rows and scroll position.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6722;
    }

}
