/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.tests.layouts;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

public class RelativeSizeInUndefinedCssLayout extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getStyles().add(".css-style { width: 520px; }");

        CssLayout cssLayout = new CssLayout();
        cssLayout.addStyleName("css-style");

        setContent(cssLayout);

        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();

        cssLayout.addComponent(formLayout);

        TextField tf = new TextField("Enter something");
        tf.setWidth("100%");
        formLayout.addComponent(tf);

    }

}
