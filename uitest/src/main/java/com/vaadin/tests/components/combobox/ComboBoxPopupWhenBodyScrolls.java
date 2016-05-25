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
package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

public class ComboBoxPopupWhenBodyScrolls extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getStyles().add(
                "body.v-generated-body { overflow: auto;height:auto;}");
        getPage()
                .getStyles()
                .add("body.v-generated-body .v-ui.v-scrollable{ overflow: visible;height:auto !important;}");
        ComboBox cb = new ComboBox();
        for (int i = 0; i < 10; i++) {
            cb.addItem("Item " + i);
        }

        Label spacer = new Label("foo");
        spacer.setHeight("2000px");
        addComponent(spacer);
        addComponent(cb);
        spacer = new Label("foo");
        spacer.setHeight("2000px");
        addComponent(spacer);
        // Chrome requires document.scrollTop (<body>)
        // Firefox + IE wants document.documentElement.scrollTop (<html>)
        getPage()
                .getJavaScript()
                .execute(
                        "document.body.scrollTop=1800;document.documentElement.scrollTop=1800;");
    }
}
