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
package com.vaadin.tests.layouts;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Layout.MarginHandler;
import com.vaadin.ui.NativeButton;

public class CssLayoutCustomCss extends TestBase implements ClickListener {

    protected Map<Component, String> css = new HashMap<>();
    private CssLayout layout;

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        layout = new CssLayout() {
            @Override
            protected String getCss(com.vaadin.ui.Component c) {
                return css.get(c);
            }
        };
        layout.setSizeFull();
        addComponent(layout);

        Button red, green;
        layout.addComponent(red = createButton("color:red"));
        layout.addComponent(createButton("color: blue"));
        layout.addComponent(green = createButton("color: green"));

        red.click();
        green.click();
        layout.addComponent(createMarginsToggle());
    }

    private Component createMarginsToggle() {
        final CheckBox cb = new CheckBox("Margins");
        cb.addValueChangeListener(
                event -> ((MarginHandler) layout).setMargin(cb.getValue()));

        return cb;
    }

    private Button createButton(String string) {
        NativeButton button = new NativeButton(string);
        css.put(button, string);
        button.addClickListener(this);
        return button;
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Button b = event.getButton();
        if (b.getCaption().contains("not ")) {
            b.setCaption(b.getCaption().substring(4));
            css.put(b, b.getCaption());
        } else {
            css.remove(b);
            b.setCaption("not " + b.getCaption());
        }
        layout.markAsDirty();

    }

}
