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

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Link;
import com.vaadin.v7.ui.TreeTable;

public class DisappearingComponents extends AbstractReindeerTestUI {

    @Override
    public void setup(VaadinRequest request) {
        final TreeTable tt = new TreeTable();
        tt.setSizeUndefined();
        tt.setWidth("100%");
        tt.setImmediate(true);
        tt.setPageLength(0);
        tt.addContainerProperty("i", Integer.class, null);
        tt.addContainerProperty("link", Link.class, null);
        Object[] items = new Object[3];
        for (int i = 0; i < items.length; i++) {
            items[i] = tt
                    .addItem(
                            new Object[] { i + 1,
                                    new Link(String.valueOf(i + 1),
                                            new ExternalResource(
                                                    "http://www.google.fi")) },
                            null);
        }
        tt.setChildrenAllowed(items[0], false);
        tt.setChildrenAllowed(items[2], false);
        tt.setParent(items[2], items[1]);

        addComponent(tt);
    }

    @Override
    protected String getTestDescription() {
        return "TreeTable column component empty after expand+collapse when pageLength is set to zero";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7808;
    }

}
