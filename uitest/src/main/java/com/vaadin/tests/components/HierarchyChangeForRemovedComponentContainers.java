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
package com.vaadin.tests.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class HierarchyChangeForRemovedComponentContainers extends TestBase {

    private HorizontalLayout mainContent;
    private VerticalLayout lo2;

    @Override
    protected void setup() {

        mainContent = new HorizontalLayout();
        mainContent.setSizeFull();

        lo2 = new VerticalLayout();
        Button button1 = new Button("asdasd1");
        button1.setHeight("90%");
        Button button2 = new Button("asdasd2");
        button2.setHeight("90%");
        lo2.addComponent(button1);
        lo2.addComponent(button2);

        compose();

        addComponent(new Button("Replace layout with button",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        compose2();
                    }
                }));
    }

    private void compose() {
        getLayout().removeAllComponents();
        getLayout().addComponent(mainContent);
        mainContent.addComponent(lo2);
        System.out.println("composed");
    }

    private void compose2() {
        getLayout().removeAllComponents();
        getLayout().addComponent(lo2);
    }

    @Override
    protected String getDescription() {
        return "HierarchyChange events should be triggered for removed layouts";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9815;
    }

}
