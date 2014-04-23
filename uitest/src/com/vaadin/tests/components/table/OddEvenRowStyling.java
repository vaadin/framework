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

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

/**
 * @author jonatan
 * 
 */
public class OddEvenRowStyling extends TestBase {

    @Override
    protected void setup() {
        Table t = new Table();
        t.setPageLength(10);
        t.addContainerProperty("foo", String.class, "");
        for (int i = 0; i < 33; i++) {
            t.addItem(i).getItemProperty("foo").setValue("bar");
        }
        addComponent(t);
    }

    @Override
    protected String getDescription() {
        return "Odd/even row styling should not change when scrolling";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7644;
    }

}
