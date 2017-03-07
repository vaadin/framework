/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.treetable;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.Container.Hierarchical;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.TreeTable;

public class TreeTablePartialUpdatesPageLength0 extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setTheme("reindeer-tests");
        TreeTable tt = new TreeTable();
        tt.addStyleName("table-equal-rowheight");
        tt.setPageLength(0);
        tt.setContainerDataSource(makeHierarchicalContainer());
        tt.setWidth("300px");
        addComponent(tt);
        tt.getParent().setHeight(null);
        tt.getParent().getParent().setHeight(null);
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

        addNodesToRoot(hc, r1, 10);
        addNodesToRoot(hc, r2, 200);
        addNodesToRoot(hc, r3, 200);
        return hc;
    }

    @SuppressWarnings("unchecked")
    private void addNodesToRoot(HierarchicalContainer hc, Object root,
            int count) {
        for (int ix = 0; ix < count; ix++) {
            Object id = hc.addItem();
            hc.getItem(id).getItemProperty("p1").setValue(String.valueOf(ix));
            hc.setParent(id, root);
        }
    }

    @Override
    protected String getTestDescription() {
        return "";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6722;
    }

}
