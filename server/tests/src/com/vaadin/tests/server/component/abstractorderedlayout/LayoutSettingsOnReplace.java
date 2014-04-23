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
package com.vaadin.tests.server.component.abstractorderedlayout;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;

/**
 * Tests for abstract layout settings which should be preserved on replace
 * component
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class LayoutSettingsOnReplace {

    @Test
    public void testExpandRatio() {
        AbstractOrderedLayout layout = new AbstractOrderedLayout() {
        };

        AbstractComponent first = new AbstractComponent() {
        };
        AbstractComponent second = new AbstractComponent() {
        };

        layout.addComponent(first);
        layout.addComponent(second);

        int ratio = 2;
        layout.setExpandRatio(first, ratio);
        layout.setExpandRatio(second, 1);

        AbstractComponent replace = new AbstractComponent() {
        };
        layout.replaceComponent(first, replace);

        Assert.assertEquals("Expand ratio for replaced component is not "
                + "the same as for previous one", ratio,
                layout.getExpandRatio(replace), 0.0001);
    }

    @Test
    public void testAlignment() {
        AbstractOrderedLayout layout = new AbstractOrderedLayout() {
        };

        AbstractComponent first = new AbstractComponent() {
        };
        AbstractComponent second = new AbstractComponent() {
        };

        layout.addComponent(first);
        layout.addComponent(second);

        Alignment alignment = Alignment.BOTTOM_RIGHT;
        layout.setComponentAlignment(first, alignment);
        layout.setComponentAlignment(second, Alignment.MIDDLE_CENTER);

        AbstractComponent replace = new AbstractComponent() {
        };
        layout.replaceComponent(first, replace);

        Assert.assertEquals("Alignment for replaced component is not "
                + "the same as for previous one", alignment,
                layout.getComponentAlignment(replace));
    }
}
