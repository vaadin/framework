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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class AddRemoveComponentTest {

    @Test
    public void testRemoveComponentFromWrongContainer()
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        List<Class<? extends ComponentContainer>> containerClasses = VaadinClasses
                .getComponentContainersSupportingAddRemoveComponent();

        Assert.assertTrue(containerClasses.size() > 0);

        // No default constructor, special case
        containerClasses.remove(CustomLayout.class);
        testRemoveComponentFromWrongContainer(new CustomLayout("dummy"));

        for (Class<? extends ComponentContainer> clazz : containerClasses) {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }
            try {
                Constructor<? extends ComponentContainer> constructor = clazz
                        .getConstructor();
                constructor.setAccessible(true);
                testRemoveComponentFromWrongContainer(
                        constructor.newInstance());
            } catch (NoSuchMethodException ignore) {
                // if there is no default CTOR, just ignore
            }
        }
    }

    private void testRemoveComponentFromWrongContainer(
            ComponentContainer componentContainer) {
        HorizontalLayout hl = new HorizontalLayout();
        Label label = new Label();
        hl.addComponent(label);

        componentContainer.removeComponent(label);
        Assert.assertEquals(
                "Parent no longer correct for " + componentContainer.getClass(),
                hl, label.getParent());
    }
}
