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
package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.Table;

public class EditableTableFocus extends TestBase {
    @Override
    public void setup() {
        Table table = new Table();

        table.addContainerProperty("TextField", String.class, null);
        table.setColumnWidth("TextField", 150);
        for (int i = 1; i < 100; i++) {
            table.addItem(new String[] { "" }, new Integer(i));
        }
        table.setEditable(true);

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "<b>IE-Problem: TextFields in table lose their focus, no input possible</b><p>"
                + "Try inputs in the table's textfields in the freshly started programm. For the moment all works fine.<p>"
                + "Then scroll the table down one page or more.<br>"
                + "Try again to make some inputs. Nothing happens...<br>"
                + "Now the textfields always lose their focus immediately after they got it and no input is taken.<p>"
                + "<b>This problem is exclusive to Microsoft's Internet Explorer!</b>";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7965;
    }
}
