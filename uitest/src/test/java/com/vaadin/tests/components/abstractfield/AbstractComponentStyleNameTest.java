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
package com.vaadin.tests.components.abstractfield;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;

public class AbstractComponentStyleNameTest {

    AbstractComponent c;

    @Before
    public void setup() {
        c = new Button();
    }

    @Test
    public void add() {
        c.addStyleName("foo");
        Assert.assertEquals("foo", c.getStyleName());
    }

    @Test
    public void addWithHeadingSpace() {
        c.addStyleName(" foo");
        Assert.assertEquals("foo", c.getStyleName());
    }

    @Test
    public void addWithTrailingSpace() {
        c.addStyleName("foo ");
        Assert.assertEquals("foo", c.getStyleName());
    }

    @Test
    public void removeWithHeadingSpace() {
        c.setStyleName("foo");
        c.removeStyleName(" foo");
        Assert.assertEquals("", c.getStyleName());
    }

    @Test
    public void removeWithTrailingSpace() {
        c.setStyleName("foo");
        c.removeStyleName("foo ");
        Assert.assertEquals("", c.getStyleName());
    }

    @Test
    public void addMultipleTimes() {
        c.addStyleName("foo");
        c.addStyleName("foo");
        Assert.assertEquals("foo", c.getStyleName());
    }

    @Test
    public void setStyleAdd() {
        c.setStyleName("foo", true);
        Assert.assertEquals("foo", c.getStyleName());
    }

    @Test
    public void setStyleMultipleAdd() {
        c.setStyleName("foo", true);
        c.setStyleName("foo", true);
        Assert.assertEquals("foo", c.getStyleName());
    }

    @Test
    public void setStyleRemove() {
        c.addStyleName("foo");
        c.setStyleName("foo", false);
        Assert.assertEquals("", c.getStyleName());
    }

    @Test
    public void setStyleMultipleRemove() {
        c.addStyleName("foo");
        c.setStyleName("foo", false);
        c.setStyleName("foo", false);
        Assert.assertEquals("", c.getStyleName());
    }

    @Test
    public void remove() {
        c.addStyleName("foo");
        c.removeStyleName("foo");
        Assert.assertEquals("", c.getStyleName());
    }

    @Test
    public void removeMultipleTimes() {
        c.addStyleName("foo");
        c.removeStyleName("foo");
        c.removeStyleName("foo");
        Assert.assertEquals("", c.getStyleName());
    }
}
