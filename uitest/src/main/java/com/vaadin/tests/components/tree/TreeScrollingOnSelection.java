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
package com.vaadin.tests.components.tree;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Tree;

public class TreeScrollingOnSelection extends TestBase {
    private static final long serialVersionUID = 4082075610259697145L;

    private static final String GREEK_ALPHABET = "greek alphabet";

    private static final String[] ALPHABET = { "Alpha", "Beta", "Gamma",
            "Delta", "Epsilon", "Zeta", "Eta", "Theta", "Iota", "Kappa",
            "Lambda", "My", "Ny", "Xi", "Omikron", "Pi", "Rho", "Sigma", "Tau",
            "Ypsilon", "Phi", "Chi", "Psi", "Omega" };

    @Override
    public void setup() {
        final Label charLabel = new Label();
        charLabel.setWidth("200px");

        Tree tree = new Tree("alphabets", getContainer());
        tree.expandItem(GREEK_ALPHABET);
        tree.addListener(new ItemClickListener() {

            private static final long serialVersionUID = 5955518276555388126L;

            @Override
            public void itemClick(ItemClickEvent event) {
                charLabel.setValue(event.getItemId().toString());
            }
        });
        tree.setImmediate(true);

        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        Panel panel = new Panel(panelLayout);
        panelLayout.addComponent(tree);
        panel.setWidth("200px");
        panel.setHeight("300px");

        addComponent(panel);

        addComponent(charLabel);
    }

    private HierarchicalContainer getContainer() {
        HierarchicalContainer container = new HierarchicalContainer();

        container.addItem(GREEK_ALPHABET);

        for (String character : ALPHABET) {
            container.addItem(character);
            container.setChildrenAllowed(character, false);
            container.setParent(character, GREEK_ALPHABET);
        }

        return container;
    }

    @Override
    protected String getDescription() {
        return "Selecting an item in the tree inside the Panel should not cause the panel scroll position to change.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6731;
    }
}
