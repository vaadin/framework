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

import java.util.ArrayList;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;

public class ListSelectJump extends AbstractReindeerTestUI {

    @Override
    public void setup(VaadinRequest request) {
        getLayout().setMargin(new MarginInfo(true, false, false, false));

        addComponent(new Label(
                "Instructions:<ol><li>Select Option #1</li><li><b>Also</b> select Option #10 (use meta-click)</li>"
                        + "<li>Leave the Option #10 visible in the scroll window</li><li>Press the button</li></ol>"
                        + "You will see the <code>ListSelect</code> scroll window jump back to the top.",
                ContentMode.HTML));
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            list.add("Option #" + i);
        }
        ListSelect<String> listSelect = new ListSelect<>(null, list);
        listSelect.setRows(5);
        listSelect.setId("listselect");
        addComponent(listSelect);
        Button button = new Button("Press Me");
        button.setId("button");
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "ListSelect jumps to top row after each client -> server contact";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10416;
    }

}
