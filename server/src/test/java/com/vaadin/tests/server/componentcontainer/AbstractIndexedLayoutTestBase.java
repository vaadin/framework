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
package com.vaadin.tests.server.componentcontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

public abstract class AbstractIndexedLayoutTestBase {

    private Layout layout;

    protected abstract Layout createLayout();

    @Before
    public void setUp() {
        layout = createLayout();
    }

    public Layout getLayout() {
        return layout;
    }

    @Test
    public void testAddRemoveComponent() {
        Label c1 = new Label();
        Label c2 = new Label();

        layout.addComponent(c1);

        assertEquals(c1, getComponent(0));
        assertEquals(1, getComponentCount());
        layout.addComponent(c2);
        assertEquals(c1, getComponent(0));
        assertEquals(c2, getComponent(1));
        assertEquals(2, getComponentCount());
        layout.removeComponent(c1);
        assertEquals(c2, getComponent(0));
        assertEquals(1, getComponentCount());
        layout.removeComponent(c2);
        assertEquals(0, getComponentCount());
    }

    protected abstract int getComponentCount();

    protected abstract Component getComponent(int index);

    protected abstract int getComponentIndex(Component c);

    @Test
    public void testGetComponentIndex() {
        Label c1 = new Label();
        Label c2 = new Label();

        layout.addComponent(c1);
        assertEquals(0, getComponentIndex(c1));
        layout.addComponent(c2);
        assertEquals(0, getComponentIndex(c1));
        assertEquals(1, getComponentIndex(c2));
        layout.removeComponent(c1);
        assertEquals(0, getComponentIndex(c2));
        layout.removeComponent(c2);
        assertEquals(-1, getComponentIndex(c2));
        assertEquals(-1, getComponentIndex(c1));
    }

    @Test
    public void testGetComponent() {
        Label c1 = new Label();
        Label c2 = new Label();

        layout.addComponent(c1);
        assertEquals(c1, getComponent(0));
        layout.addComponent(c2);
        assertEquals(c1, getComponent(0));
        assertEquals(c2, getComponent(1));
        layout.removeComponent(c1);
        assertEquals(c2, getComponent(0));
        layout.removeComponent(c2);
        try {
            getComponent(0);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }
}
