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
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;

public class ButtonsWaiAria extends ComponentTestCase<Button> {

    @Override
    protected Class<Button> getTestClass() {
        return Button.class;
    }

    @Override
    protected void initializeComponents() {

        Button l;
        boolean nat = false;

        l = createButton("Default Button", nat);
        addTestComponent(l);

        l = createButton("Icon Button, empty alt", nat);
        l.setIcon(ICON_16_USER_PNG_CACHEABLE);
        l.setDescription("Empty alt text");
        addTestComponent(l);

        l = createButton("Icon Button with alt", nat);
        l.setIcon(ICON_16_USER_PNG_CACHEABLE, "user icon");
        addTestComponent(l);

        l = createButton("Tooltip Button", nat);
        l.setDescription("Tooltip");
        addTestComponent(l);

        l = createButton("Another tooltip", nat);
        l.setDescription("Another");
        addTestComponent(l);
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
