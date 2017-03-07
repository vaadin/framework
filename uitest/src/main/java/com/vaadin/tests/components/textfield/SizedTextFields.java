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
package com.vaadin.tests.components.textfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

public class SizedTextFields extends TestBase {

    @SuppressWarnings("deprecation")
    @Override
    protected void setup() {

        TextField tf;

        VerticalLayout vl;

        CssLayout cssLayout = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                return "margin-top: 20px; background:red;";
            }
        };

        vl = new VerticalLayout();
        vl.setHeight("40px");
        vl.setWidth("200px");

        tf = new TextField();
        tf.setSizeFull();
        vl.addComponent(tf);
        vl.setCaption("Fullsize textfield in 40px height 200px width box");
        cssLayout.addComponent(vl);

        vl = new VerticalLayout();
        vl.setHeight("40px");
        vl.setWidth("200px");

        TextArea ta = new TextArea();
        ta.setRows(2); // make it text area, instead of oneliner
        ta.setSizeFull();
        vl.addComponent(ta);
        vl.setCaption("Fullsize textarea in 100px height 200px width box");
        cssLayout.addComponent(vl);

        vl = new VerticalLayout();
        vl.setSizeUndefined();

        tf = new TextField();
        vl.addComponent(tf);
        vl.setCaption("Normal textfield in natural size.");
        cssLayout.addComponent(vl);

        getLayout().addComponent(cssLayout);

    }

    @Override
    protected String getDescription() {
        return "TextField sizing is problematic with old IE browsers. "
                + "This test is to verify correct size. No red color should "
                + "be visible in IE (at least with default windows themes) "
                + "and textfields should not look clipped.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2058;
    }

}
