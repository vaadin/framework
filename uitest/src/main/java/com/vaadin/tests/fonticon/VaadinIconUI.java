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
package com.vaadin.tests.fonticon;

import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("tests-valo")
public class VaadinIconUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();

        TextField name = new TextField("Name");
        name.setIcon(VaadinIcons.USER);
        name.addStyleName("blueicon");
        layout.addComponent(name);

        // Button allows specifying icon resource in constructor
        Button ok = new Button("OK", VaadinIcons.CHECK);
        ok.addStyleName("blueicon");
        layout.addComponent(ok);

        setContent(layout);

        Label label = new Label("I " + VaadinIcons.HEART.getHtml() + " Vaadin",
                ContentMode.HTML);
        label.addStyleName("redicon");
        layout.addComponent(label);

        TextField amount = new TextField("Amount (in "
                + new String(
                        Character.toChars(VaadinIcons.DOLLAR.getCodepoint()))
                + ")");
        amount.addStyleName("amount");
        layout.addComponent(amount);
    }

}
