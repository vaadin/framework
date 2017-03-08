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
package com.vaadin.tests.components.listselect;

import java.util.Arrays;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.ListSelect;

public class ListSelectPrimaryStylename extends TestBase {

    @Override
    protected void setup() {
        final ListSelect list = new ListSelect("Caption",
                Arrays.asList("Option 1", "Option 2", "Option 3"));
        list.setPrimaryStyleName("my-list");
        addComponent(list);

        addComponent(new Button("Change primary stylename",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        list.setPrimaryStyleName("my-second-list");
                    }
                }));

    }

    @Override
    protected String getDescription() {
        return "List select should should support primary stylenames both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9907;
    }

}
