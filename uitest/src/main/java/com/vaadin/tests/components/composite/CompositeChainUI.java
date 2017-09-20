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
package com.vaadin.tests.components.composite;

import java.util.Iterator;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Composite;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class CompositeChainUI extends AbstractTestUIWithLog {

    private Label innermostComponent;
    private Composite innerComposite;
    private Composite outerComposite;
    private VerticalLayout container;
    private HorizontalLayout layout;

    @Override
    protected void setup(VaadinRequest request) {

        createComposite();
        layout = new HorizontalLayout(outerComposite);
        container = new VerticalLayout(layout);
        addComponent(container);

        Button updateCaption = new Button("Update caption");
        updateCaption.addClickListener(e -> {
            innermostComponent
                    .setCaption(innermostComponent.getCaption() + " - updated");
        });
        addComponent(updateCaption);
        Button replaceWithAnotherComposite = new Button(
                "Replace with another Composite", e -> {
                    Composite oldOuter = outerComposite;
                    createComposite();
                    layout.replaceComponent(oldOuter, outerComposite);
                });
        addComponent(replaceWithAnotherComposite);
        logHierarchy();
    }

    private void createComposite() {
        innermostComponent = new Label("Label text");
        innermostComponent.setCaption("Label caption");
        innermostComponent.setId("innermost");

        innerComposite = new Composite(innermostComponent);
        outerComposite = new Composite(innerComposite);
    }

    private void logHierarchy() {
        String msg = "Hierarchy: ";
        if (container != null) {
            msg += getHierarchy(container);
        }
        log(msg);
    }

    private static String getHierarchy(Component component) {
        String msg = component.getClass().getSimpleName();
        if (component instanceof HasComponents) {

            Iterator<Component> iterator = ((HasComponents) component)
                    .iterator();
            if (iterator.hasNext()) {
                Component content = iterator.next();
                if (content != null) {
                    msg += " -> " + getHierarchy(content);
                }
            }
        }
        return msg;
    }

}
