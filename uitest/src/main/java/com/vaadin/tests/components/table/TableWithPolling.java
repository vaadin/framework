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
import com.vaadin.ui.Table;

public class TableWithPolling extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "Polling shouldn't affect table column resizing in any way.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13432;
    }

    @Override
    protected void setup(VaadinRequest request) {

        Table table = new Table("This is my Table");

        table.addContainerProperty("First Name", String.class, null);
        table.addContainerProperty("Last Name", String.class, null);
        table.addContainerProperty("Year", Integer.class, null);

        table.addItem(new Object[] { "Nicolaus", "Copernicus",
                new Integer(1473) }, new Integer(1));
        table.addItem(new Object[] { "Tycho", "Brahe", new Integer(1546) },
                new Integer(2));
        table.addItem(new Object[] { "Giordano", "Bruno", new Integer(1548) },
                new Integer(3));
        table.addItem(new Object[] { "Galileo", "Galilei", new Integer(1564) },
                new Integer(4));
        table.addItem(new Object[] { "Johannes", "Kepler", new Integer(1571) },
                new Integer(5));
        table.addItem(new Object[] { "Isaac", "Newton", new Integer(1643) },
                new Integer(6));

        addComponent(table);

        setPollInterval(1000);
    }
}
