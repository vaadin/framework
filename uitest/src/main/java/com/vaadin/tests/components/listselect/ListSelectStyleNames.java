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

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ListSelectStyleNames extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        ListSelect<String> testselect = new ListSelect<>();
        testselect.setItems("abc", "def", "ghi");
        testselect.addStyleName("custominitial");
        addComponent(testselect);

        NativeSelect<String> nativeSelect = new NativeSelect<>();
        nativeSelect.setItems("abc", "def", "ghi");
        nativeSelect.addStyleName("custominitial");
        addComponent(nativeSelect);

        Button button = new Button("Add style 'new'", e -> {
            testselect.addStyleName("new");
            nativeSelect.addStyleName("new");
        });
        button.setId("add");
        addComponent(button);

        button = new Button("Change primary style to 'newprimary'", e -> {
            testselect.setPrimaryStyleName("newprimary");
            nativeSelect.setPrimaryStyleName("newprimary");
        });
        button.setId("changeprimary");
        addComponent(button);
    }

}
