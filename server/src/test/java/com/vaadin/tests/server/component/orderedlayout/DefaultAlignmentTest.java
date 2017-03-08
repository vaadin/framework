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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class DefaultAlignmentTest {

    private VerticalLayout verticalLayout;
    private HorizontalLayout horizontalLayout;

    @Before
    public void setup() {
        verticalLayout = new VerticalLayout();
        horizontalLayout = new HorizontalLayout();
    }

    @Test
    public void testDefaultAlignmentVerticalLayout() {
        testDefaultAlignment(verticalLayout);
    }

    @Test
    public void testDefaultAlignmentHorizontalLayout() {
        testDefaultAlignment(horizontalLayout);
    }

    public void testDefaultAlignment(AbstractOrderedLayout layout) {
        Label label = new Label("A label");
        TextField tf = new TextField("A TextField");
        layout.addComponent(label);
        layout.addComponent(tf);
        Assert.assertEquals(Alignment.TOP_LEFT,
                layout.getComponentAlignment(label));
        Assert.assertEquals(Alignment.TOP_LEFT,
                layout.getComponentAlignment(tf));
    }

    @Test
    public void testAlteredDefaultAlignmentVerticalLayout() {
        testAlteredDefaultAlignment(verticalLayout);
    }

    @Test
    public void testAlteredDefaultAlignmentHorizontalLayout() {
        testAlteredDefaultAlignment(horizontalLayout);
    }

    public void testAlteredDefaultAlignment(AbstractOrderedLayout layout) {
        Label label = new Label("A label");
        TextField tf = new TextField("A TextField");
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        layout.addComponent(label);
        layout.addComponent(tf);
        Assert.assertEquals(Alignment.MIDDLE_CENTER,
                layout.getComponentAlignment(label));
        Assert.assertEquals(Alignment.MIDDLE_CENTER,
                layout.getComponentAlignment(tf));
    }
}
