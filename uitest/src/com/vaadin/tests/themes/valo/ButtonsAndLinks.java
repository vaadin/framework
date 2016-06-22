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
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Vaadin Ltd
 */
public class ButtonsAndLinks extends VerticalLayout implements View {
    /**
 * 
 */
    public ButtonsAndLinks() {
        setMargin(true);

        Label h1 = new Label("Buttons");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        row.setSpacing(true);
        addComponent(row);

        Button button = new Button("Normal");
        row.addComponent(button);

        button = new Button("Disabled");
        button.setEnabled(false);
        row.addComponent(button);

        button = new Button("Primary");
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        row.addComponent(button);

        button = new Button("Friendly");
        button.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        row.addComponent(button);

        button = new Button("Danger");
        button.addStyleName(ValoTheme.BUTTON_DANGER);
        row.addComponent(button);

        TestIcon testIcon = new TestIcon(10);
        button = new Button("Small");
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.setIcon(testIcon.get());
        row.addComponent(button);

        button = new Button("Large");
        button.addStyleName(ValoTheme.BUTTON_LARGE);
        button.setIcon(testIcon.get());
        row.addComponent(button);

        button = new Button("Top");
        button.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
        button.setIcon(testIcon.get());
        row.addComponent(button);

        button = new Button("Image icon");
        button.setIcon(testIcon.get(true, 16));
        row.addComponent(button);

        button = new Button("Image icon");
        button.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        button.setIcon(testIcon.get(true));
        row.addComponent(button);

        button = new Button("Photos");
        button.setIcon(testIcon.get());
        row.addComponent(button);

        button = new Button();
        button.setIcon(testIcon.get());
        button.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        row.addComponent(button);

        button = new Button("Borderless");
        button.setIcon(testIcon.get());
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        row.addComponent(button);

        button = new Button("Borderless, colored");
        button.setIcon(testIcon.get());
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        row.addComponent(button);

        button = new Button("Quiet");
        button.setIcon(testIcon.get());
        button.addStyleName(ValoTheme.BUTTON_QUIET);
        row.addComponent(button);

        button = new Button("Link style");
        button.setIcon(testIcon.get());
        button.addStyleName(ValoTheme.BUTTON_LINK);
        row.addComponent(button);

        button = new Button("Icon on right");
        button.setIcon(testIcon.get());
        button.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        row.addComponent(button);

        CssLayout group = new CssLayout();
        group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        row.addComponent(group);

        button = new Button("One");
        group.addComponent(button);
        button = new Button("Two");
        group.addComponent(button);
        button = new Button("Three");
        group.addComponent(button);

        button = new Button("Tiny");
        button.addStyleName(ValoTheme.BUTTON_TINY);
        row.addComponent(button);

        button = new Button("Huge");
        button.addStyleName(ValoTheme.BUTTON_HUGE);
        row.addComponent(button);

        NativeButton nbutton = new NativeButton("Native");
        row.addComponent(nbutton);

        h1 = new Label("Links");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        row = new HorizontalLayout();
        row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
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
        link.addStyleName(ValoTheme.LINK_SMALL);
        row.addComponent(link);

        link = new Link("Large", new ExternalResource("https://vaadin.com"));
        link.addStyleName(ValoTheme.LINK_LARGE);
        row.addComponent(link);

        link = new Link(null, new ExternalResource("https://vaadin.com"));
        link.setIcon(testIcon.get());
        link.addStyleName(ValoTheme.LINK_LARGE);
        row.addComponent(link);

        link = new Link("Disabled", new ExternalResource("https://vaadin.com"));
        link.setIcon(testIcon.get());
        link.setEnabled(false);
        row.addComponent(link);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
