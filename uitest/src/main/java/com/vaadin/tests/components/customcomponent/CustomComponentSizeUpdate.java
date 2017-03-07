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
package com.vaadin.tests.components.customcomponent;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.NativeButton;

public class CustomComponentSizeUpdate extends TestBase {

    @Override
    protected void setup() {
        NativeButton nb = new NativeButton(
                "100%x100% button. Click to reduce CustomComponent size");
        nb.setSizeFull();

        final CustomComponent cc = new CustomComponent(nb);
        cc.setWidth("500px");
        cc.setHeight("500px");

        nb.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                cc.setWidth((cc.getWidth() - 20) + "px");
                cc.setHeight((cc.getHeight() - 20) + "px");

            }
        });

        addComponent(cc);

    }

    @Override
    protected String getDescription() {
        return "Click the button to reduce the size of the parent. The button should be resized to fit the parent.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3705;
    }

}
