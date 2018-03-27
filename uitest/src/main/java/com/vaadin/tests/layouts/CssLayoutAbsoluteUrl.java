/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.layouts;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class CssLayoutAbsoluteUrl extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label("Hello");
        label.setId("myLabel");
        addComponent(new CssLayout(label) {
            @Override
            protected String getCss(Component c) {
                return "color: blue; background-image: url(\"about:blank\");";
            }
        });
    }

}
