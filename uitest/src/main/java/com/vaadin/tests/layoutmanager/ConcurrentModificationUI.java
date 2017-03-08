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
package com.vaadin.tests.layoutmanager;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

public class ConcurrentModificationUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        Panel panel = new Panel();
        setContent(panel);

        FormLayout form = new FormLayout();
        panel.setContent(form);

        HorizontalLayout horizLyt = new HorizontalLayout();
        form.addComponent(horizLyt);

        CssLayout cssLyt = new CssLayout();
        horizLyt.addComponent(cssLyt);
        horizLyt.setComponentAlignment(cssLyt, Alignment.MIDDLE_LEFT);
    }

}
