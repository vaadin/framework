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
package com.vaadin.tests.contextclick;

import com.vaadin.annotations.Widgetset;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

@Widgetset(TestingWidgetSet.NAME)
public class BrowserContextMenuInSubComponent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Panel panel = new Panel();

        VerticalLayout layout = new VerticalLayout();
        final TextArea textArea = new TextArea();
        // Make TextArea show regular context menu instead of firing the
        // server-side event.
        BrowserContextMenuExtension.extend(textArea);
        final Button button = new Button("Submit", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Notification.show(textArea.getValue());
            }
        });

        layout.addComponent(textArea);
        layout.addComponent(button);

        panel.setContent(layout);

        panel.addContextClickListener(new ContextClickListener() {

            @Override
            public void contextClick(ContextClickEvent event) {
                button.click();
            }
        });

        addComponent(panel);
    }

    /**
     * A simple extension for making extended component stop propagation of the
     * context click events, so the browser will handle the context click and
     * show its own context menu.
     */
    public static class BrowserContextMenuExtension extends AbstractExtension {
        private BrowserContextMenuExtension(AbstractComponent c) {
            super(c);
        }

        public static BrowserContextMenuExtension extend(AbstractComponent c) {
            return new BrowserContextMenuExtension(c);
        }
    }

}
