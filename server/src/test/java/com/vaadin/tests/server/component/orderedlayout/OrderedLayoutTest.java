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
package com.vaadin.tests.server.component.orderedlayout;

import java.util.Iterator;

import org.junit.Test;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class OrderedLayoutTest {

    @Test
    public void testVLIteration() {
        testIndexing(new VerticalLayout(), 10);
    }

    @Test
    public void testHLIteration() {
        testIndexing(new HorizontalLayout(), 12);
    }

    public void testIndexing(AbstractOrderedLayout aol, int nrComponents) {
        Component[] components = generateComponents(nrComponents);
        for (Component c : components) {
            aol.addComponent(c);
        }
        for (int i = 0; i < nrComponents; i++) {
            assert (aol.getComponent(i) == components[i]);
            assert (aol.getComponentIndex(components[i]) == i);
        }

        // Iteration should be in indexed order
        int idx = 0;
        for (Iterator<Component> i = aol.getComponentIterator(); i.hasNext();) {
            Component c = i.next();
            assert (aol.getComponentIndex(c) == idx++);
        }
    }

    private Component[] generateComponents(int nr) {
        Component[] components = new Component[nr];
        for (int i = 0; i < nr; i++) {
            components[i] = new Label("" + i);
        }

        return components;
    }
}
