/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * There shouldn't be any attempts to refresh table's cells if the table isn't
 * attached.
 * 
 * @author Vaadin Ltd
 */
public class RefreshRenderedCellsOnlyIfAttached extends AbstractTestUI {

    VerticalLayout layout;
    boolean check;

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setMargin(true);
        check = false;
        layout = new VerticalLayout();
        final Label l1 = new Label("default");
        l1.setId("label");
        final Label l2 = new Label("should be: default");
        final Table t = new Table() {
            /*
             * (non-Javadoc)
             * 
             * @see com.vaadin.ui.Table#refreshRenderedCells()
             */
            @Override
            protected void refreshRenderedCells() {
                boolean original = isRowCacheInvalidated();
                super.refreshRenderedCells();
                if (check) {
                    l1.setValue("original: " + original + ", now: "
                            + isRowCacheInvalidated());
                    l2.setValue("should be: false & false");
                }
            }
        };
        t.addContainerProperty("text", String.class, "");
        t.addItem(new Object[] { "Foo" }, "foo");
        t.setId("table");
        layout.addComponent(t);
        addComponent(l1);
        addComponent(l2);
        addComponent(layout);

        Button b = new Button("Detach table", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                check = true;
                removeTableParent();
                // call refreshRenderedCells
                t.setColumnCollapsingAllowed(true);
            }
        });
        b.setId("button");
        addComponent(b);
    }

    /**
     * Remove Table's parent component.
     * 
     */
    protected void removeTableParent() {
        removeComponent(layout);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "There shouldn't be any attempts to refresh table's cells if the table isn't attached.";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 9138;
    }

}
