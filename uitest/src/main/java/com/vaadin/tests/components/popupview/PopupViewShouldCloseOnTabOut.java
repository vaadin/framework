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
package com.vaadin.tests.components.popupview;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

@SuppressWarnings("serial")
public class PopupViewShouldCloseOnTabOut extends TestBase {

    @Override
    protected String getDescription() {
        return "The PopupView should close when the user moves focus away from it using the TAB key.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5059;
    }

    @Override
    protected void setup() {
        PopupView pv = new PopupView(new Content() {

            @Override
            public String getMinimizedValueAsHTML() {
                return "<b>click me</b>";
            }

            @Override
            public Component getPopupComponent() {
                VerticalLayout vl = new VerticalLayout();
                TextField field1 = new TextField();
                field1.setValue("one");
                field1.focus();
                vl.addComponent(field1);
                TextField field2 = new TextField();
                field2.setValue("two");
                vl.addComponent(field2);
                vl.setWidth("600px");
                return vl;
            }
        });
        addComponent(pv);
        TextField main = new TextField();
        main.setValue("main");
        addComponent(main);
        TextField main2 = new TextField();
        main2.setValue("main2");
        addComponent(main2);
    }

}
