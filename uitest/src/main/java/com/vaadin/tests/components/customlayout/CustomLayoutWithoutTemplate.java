/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.customlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;

public class CustomLayoutWithoutTemplate extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CustomLayout cl = new CustomLayout("missing-layout-file.html");
        cl.addComponent(new Label("This Label should be visible."), "foo");
        cl.addComponent(new Button("And this Button too."), "bar");

        addComponent(cl);
    }

    @Override
    protected String getTestDescription() {
        return "Verify that CustomLayout renders child components even if the template is missing.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8696;
    }
}
