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
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.ui.TreeTable;

public class MinimalWidthColumns extends AbstractTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        TreeTable tt = new TreeTable();
        tt.addContainerProperty("Foo", String.class, "");
        tt.addContainerProperty("Bar", String.class, "");

        Object item1 = tt.addItem(new Object[] { "f", "Bar" }, null);
        Object item2 = tt.addItem(new Object[] { "Foo2", "Bar2" }, null);

        tt.setParent(item2, item1);

        tt.setColumnWidth("Foo", 0);
        tt.setColumnWidth("Bar", 50);
        tt.setWidth("300px");
        addComponent(tt);
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(15118);
    }

    @Override
    protected String getTestDescription() {
        return "There should be no 1px discrepancy between vertical borders in headers and rows";
    }

}
