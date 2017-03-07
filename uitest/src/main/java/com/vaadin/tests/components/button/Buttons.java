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
package com.vaadin.tests.components.button;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;

public class Buttons extends ComponentTestCase<Button> {

    @Override
    protected Class<Button> getTestClass() {
        return Button.class;
    }

    @Override
    protected void initializeComponents() {

        Button l;
        for (boolean nat : new boolean[] { false, true }) {
            l = createButton("This is an undefined wide button", nat);
            l.setWidth(null);
            addTestComponent(l);

            l = createButton(
                    "This is an undefined wide button with fixed 100px height",
                    nat);
            l.setWidth(null);
            l.setHeight("100px");
            addTestComponent(l);

            l = createButton(
                    "This is a 200px wide simple button with a much longer caption",
                    nat);
            l.setWidth("200px");
            addTestComponent(l);

            l = createButton(
                    "This is a 100% wide simple button " + LoremIpsum.get(1500),
                    nat);
            l.setWidth("100%");
            addTestComponent(l);

            l = createButton(
                    "This is a 100% wide button with fixed 65px height. "
                            + LoremIpsum.get(5000),
                    nat);
            l.setWidth("100%");
            l.setHeight("65px");

            addTestComponent(l);
        }

    }

    private Button createButton(String text, boolean nativeButton) {
        Button b;
        if (nativeButton) {
            b = new NativeButton(text);
        } else {
            b = new Button(text);
        }

        return b;
    }

    @Override
    protected String getTestDescription() {
        return "A generic test for Buttons in different configurations";
    }

}
