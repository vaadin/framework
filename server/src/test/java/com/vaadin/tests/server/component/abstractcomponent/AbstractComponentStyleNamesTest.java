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
package com.vaadin.tests.server.component.abstractcomponent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.ui.AbstractComponent;

public class AbstractComponentStyleNamesTest {

    @Test
    public void testSetMultiple() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2");
        assertEquals(component.getStyleName(), "style1 style2");
    }

    @Test
    public void testSetAdd() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1");
        component.addStyleName("style2");
        assertEquals(component.getStyleName(), "style1 style2");
    }

    @Test
    public void testAddSame() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2");
        component.addStyleName("style1");
        assertEquals(component.getStyleName(), "style1 style2");
    }

    @Test
    public void testSetRemove() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2");
        component.removeStyleName("style1");
        assertEquals(component.getStyleName(), "style2");
    }

    @Test
    public void testAddRemove() {
        AbstractComponent component = getComponent();
        component.addStyleName("style1");
        component.addStyleName("style2");
        component.removeStyleName("style1");
        assertEquals(component.getStyleName(), "style2");
    }

    @Test
    public void testRemoveMultipleWithExtraSpaces() {
        AbstractComponent component = getComponent();
        component.setStyleName("style1 style2 style3");
        component.removeStyleName(" style1  style3 ");
        assertEquals(component.getStyleName(), "style2");
    }

    @Test
    public void testSetWithExtraSpaces() {
        AbstractComponent component = getComponent();
        component.setStyleName(" style1  style2 ");
        assertEquals(component.getStyleName(), "style1 style2");
    }

    private AbstractComponent getComponent() {
        return new AbstractComponent() {
        };
    }
}
