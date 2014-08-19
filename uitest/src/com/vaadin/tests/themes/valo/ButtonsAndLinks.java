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
package com.vaadin.tests.themes.valo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ButtonsAndLinks extends VerticalLayout implements View {
    /**
 * 
 */
    public ButtonsAndLinks() {
        setMargin(true);

        Label h1 = new Label("Buttons");
        h1.addStyleName("h1");
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName("wrapping");
        row.setSpacing(true);
        addComponent(row);

        Button button = new Button("Normal");
        row.addComponent(button);

        button = new Button("Disabled");
        button.setEnabled(false);
        row.addComponent(button);

        button = new Button("Primary");
        button.addStyleName("primary");
        row.addComponent(button);

        button = new Button("Friendly");
        button.addStyleName("friendly");
        row.addComponent(button);

        button = new Button("Danger");
        button.addStyleName("danger");
        row.addComponent(button);

        TestIcon testIcon = new TestIcon(10);
        button = new Button("Small");
        button.addStyleName("small");
        button.setIcon(testIcon.get());
        row.addComponent(button);

        button = new Button("Large");
        button.addStyleName("large");
        button.setIcon(testIcon.get());
        row.addComponent(button);

        button = new Button("Top");
        button.addStyleName("icon-align-top");
        button.setIcon(testIcon.get());
        row.addComponent(button);

        button = new Button("Image icon");
        button.setIcon(testIcon.get(true, 16));
        row.addComponent(button);

        button = new Button("Image icon");
        button.addStyleName("icon-align-right");
        button.setIcon(testIcon.get(true));
        row.addComponent(button);

        button = new Button("Photos");
        button.setIcon(testIcon.get());
        row.addComponent(button);

        button = new Button();
        button.setIcon(testIcon.get());
        button.addStyleName("icon-only");
        row.addComponent(button);

        button = new Button("Borderless");
        button.setIcon(testIcon.get());
        button.addStyleName("borderless");
        row.addComponent(button);

        button = new Button("Borderless, colored");
        button.setIcon(testIcon.get());
        button.addStyleName("borderless-colored");
        row.addComponent(button);

        button = new Button("Quiet");
        button.setIcon(testIcon.get());
        button.addStyleName("quiet");
        row.addComponent(button);

        button = new Button("Link style");
        button.setIcon(testIcon.get());
        button.addStyleName("link");
        row.addComponent(button);

        button = new Button("Icon on right");
        button.setIcon(testIcon.get());
        button.addStyleName("icon-align-right");
        row.addComponent(button);

        CssLayout group = new CssLayout();
        group.addStyleName("v-component-group");
        row.addComponent(group);

        button = new Button("One");
        group.addComponent(button);
        button = new Button("Two");
        group.addComponent(button);
        button = new Button("Three");
        group.addComponent(button);

        button = new Button("Tiny");
        button.addStyleName("tiny");
        row.addComponent(button);

        button = new Button("Huge");
        button.addStyleName("huge");
        row.addComponent(button);

        NativeButton nbutton = new NativeButton("Native");
        row.addComponent(nbutton);

        h1 = new Label("Links");
        h1.addStyleName("h1");
        addComponent(h1);

        row = new HorizontalLayout();
        row.addStyleName("wrapping");
        row.setSpacing(true);
        addComponent(row);

        Link link = new Link("vaadin.com", new ExternalResource(
                "https://vaadin.com"));
        row.addComponent(link);

        link = new Link("Link with icon", new ExternalResource(
                "https://vaadin.com"));
        link.addStyleName("color3");
        link.setIcon(testIcon.get());
        row.addComponent(link);

        link = new Link("Small", new ExternalResource("https://vaadin.com"));
        link.addStyleName("small");
        row.addComponent(link);

        link = new Link("Large", new ExternalResource("https://vaadin.com"));
        link.addStyleName("large");
        row.addComponent(link);

        link = new Link(null, new ExternalResource("https://vaadin.com"));
        link.setIcon(testIcon.get());
        link.addStyleName("large");
        row.addComponent(link);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
