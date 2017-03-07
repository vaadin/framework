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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;

public class HorizontalLayoutTest extends AbstractIndexedLayoutTestBase {

    @Override
    protected Layout createLayout() {
        return new HorizontalLayout();
    }

    @Override
    public HorizontalLayout getLayout() {
        return (HorizontalLayout) super.getLayout();
    }

    @Override
    protected Component getComponent(int index) {
        return getLayout().getComponent(index);
    }

    @Override
    protected int getComponentIndex(Component c) {
        return getLayout().getComponentIndex(c);
    }

    @Override
    protected int getComponentCount() {
        return getLayout().getComponentCount();
    }

    @Test
    public void addAndExpand_basicCase() {
        Button b1 = new Button();
        Button b2 = new Button();
        Button b3 = new Button();

        HorizontalLayout layout = getLayout();
        layout.addComponents(b3, b2);

        layout.addComponentsAndExpand(b1, b2);

        Assert.assertEquals(3, layout.getComponentCount());

        Assert.assertSame(b3, layout.getComponent(0));
        Assert.assertSame(b1, layout.getComponent(1));
        Assert.assertSame(b2, layout.getComponent(2));

        Assert.assertEquals(0, layout.getExpandRatio(b3), 0);
        Assert.assertEquals(1, layout.getExpandRatio(b1), 0);
        Assert.assertEquals(1, layout.getExpandRatio(b2), 0);

        Assert.assertEquals(-1, b3.getWidth(), 0);
        Assert.assertEquals(100, b1.getWidth(), 0);
        Assert.assertEquals(100, b2.getWidth(), 0);
    }

    @Test
    public void addAndExpand_undefinedHeightUpdated() {
        HorizontalLayout layout = getLayout();

        Assert.assertEquals(-1, layout.getWidth(), 0);

        layout.addComponentsAndExpand();

        Assert.assertEquals(100, layout.getWidth(), 0);
    }

    @Test
    public void addAndExpand_definedHeightPreserved() {
        HorizontalLayout layout = getLayout();

        layout.setWidth("150px");

        layout.addComponentsAndExpand();

        Assert.assertEquals(150, layout.getWidth(), 0);
    }

}
