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

package com.vaadin.tests;

import java.util.ArrayList;
import java.util.Iterator;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * This Component contains some simple test to see that component updates its
 * contents propertly.
 * 
 * @author Vaadin Ltd.
 */
public class TestForChildComponentRendering extends CustomComponent {

    private final VerticalLayout main;

    public TestForChildComponentRendering() {

        main = new VerticalLayout();
        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(new Label("SDFGFHFHGJGFDSDSSSGFDD"));

        final Link l = new Link();
        l.setCaption("Siirry Vaadiniin");
        l.setResource(new ExternalResource("http://www.vaadin.com/"));
        l.setTargetHeight(200);
        l.setTargetWidth(500);
        l.setTargetBorder(Link.TARGET_BORDER_MINIMAL);
        main.addComponent(l);

        final Select se = new Select();
        se.setCaption("VALITSET TÄSTÄ");
        se.addItem("valinta1");
        se.addItem("Valinta 2");

        Button b = new Button("refresh view", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                createNewView();
            }
        });
        main.addComponent(b);

        b = new Button("reorder view", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                randomReorder();
            }
        });
        main.addComponent(b);

        b = new Button("remove randomly one component",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        removeRandomComponent();
                    }
                });
        main.addComponent(b);

    }

    public void randomReorder() {
        final Iterator<Component> it = main.getComponentIterator();
        final ArrayList<Component> components = new ArrayList<Component>();
        while (it.hasNext()) {
            components.add(it.next());
        }

        final VerticalLayout v = main;
        v.removeAllComponents();

        for (int i = components.size(); i > 0; i--) {
            final int index = (int) (Math.random() * i);
            v.addComponent(components.get(index));
            components.remove(index);
        }
    }

    public void removeRandomComponent() {
        final Iterator<Component> it = main.getComponentIterator();
        final ArrayList<Component> components = new ArrayList<Component>();
        while (it.hasNext()) {
            components.add(it.next());
        }
        final int size = components.size();
        final int index = (int) (Math.random() * size);
        main.removeComponent(components.get(index));

    }

}
