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
package com.vaadin.tests.server.component.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class LegacyUIAddRemoveComponentsTest {

    private static class TestUI extends LegacyWindow {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    @Test
    public void addComponent() {
        TestUI ui = new TestUI();
        Component c = new Label("abc");

        ui.addComponent(c);

        assertSame(c.getParent(), ui.iterator().next());
        assertSame(c, ui.getContent().iterator().next());
        assertEquals(1, ui.getComponentCount());
        assertEquals(1, ui.getContent().getComponentCount());
    }

    @Test
    public void removeComponent() {
        TestUI ui = new TestUI();
        Component c = new Label("abc");

        ui.addComponent(c);

        ui.removeComponent(c);

        assertEquals(ui.getContent(), ui.iterator().next());
        assertFalse(ui.getContent().iterator().hasNext());
        assertEquals(1, ui.getComponentCount());
        assertEquals(0, ui.getContent().getComponentCount());
    }

    @Test
    public void replaceComponent() {
        TestUI ui = new TestUI();
        Component c = new Label("abc");
        Component d = new Label("def");

        ui.addComponent(c);

        ui.replaceComponent(c, d);

        assertSame(d.getParent(), ui.iterator().next());
        assertSame(d, ui.getContent().iterator().next());
        assertEquals(1, ui.getComponentCount());
        assertEquals(1, ui.getContent().getComponentCount());
    }
}
