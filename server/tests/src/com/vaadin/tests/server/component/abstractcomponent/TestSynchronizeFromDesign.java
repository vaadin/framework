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
package com.vaadin.tests.server.component.abstractcomponent;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case for the properties of the abstract component
 * 
 * @since
 * @author Vaadin Ltd
 */
public class TestSynchronizeFromDesign extends TestCase {

    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new DesignContext();
    }

    public void testSynchronizeId() {
        Node design = createDesign("id", "testId");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals("testId", component.getId());
    }

    public void testSynchronizePrimaryStyleName() {
        Node design = createDesign("primary-style-name", "test-style");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals("test-style", component.getPrimaryStyleName());
    }

    public void testSynchronizeCaption() {
        Node design = createDesign("caption", "test-caption");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals("test-caption", component.getCaption());
    }

    public void testSynchronizeLocale() {
        Node design = createDesign("locale", "fi");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals("fi", component.getLocale().getLanguage());
    }

    public void testSynchronizeExternalIcon() {
        Node design = createDesign("icon", "http://example.com/example.gif");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertTrue("Incorrect resource type returned", component.getIcon()
                .getClass().isAssignableFrom(ExternalResource.class));
    }

    public void testSynchronizeThemeIcon() {
        Node design = createDesign("icon", "theme://example.gif");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertTrue("Incorrect resource type returned", component.getIcon()
                .getClass().isAssignableFrom(ThemeResource.class));
    }

    public void testSynchronizeFileResource() {
        Node design = createDesign("icon", "img/example.gif");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertTrue("Incorrect resource type returned", component.getIcon()
                .getClass().isAssignableFrom(FileResource.class));
    }

    public void testSynchronizeImmediate() {
        Node design = createDesign("immediate", "true");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals(true, component.isImmediate());
    }

    public void testSynchronizeDescription() {
        Node design = createDesign("description", "test-description");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals("test-description", component.getDescription());
    }

    public void testSynchronizeSizeFull() {
        Node design = createDesign("size-full", "");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals(100, component.getWidth(), 0.1f);
        assertEquals(100, component.getHeight(), 0.1f);
    }

    public void testSynchronizeSizeAuto() {
        Node design = createDesign("size-auto", "");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals(-1, component.getWidth(), 0.1f);
        assertEquals(-1, component.getHeight(), 0.1f);
    }

    public void testSynchronizeHeightFull() {
        Node design = createDesign("height-full", "");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals(100, component.getHeight(), 0.1f);
    }

    public void testSynchronizeHeightAuto() {
        Node design = createDesign("height-auto", "");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals(-1, component.getHeight(), 0.1f);
    }

    public void testSynchronizeWidthFull() {
        Node design = createDesign("width-full", "");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals(100, component.getWidth(), 0.1f);
    }

    public void testSynchronizeWidthAuto() {
        Node design = createDesign("width-auto", "");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals(-1, component.getWidth(), 0.1f);
    }

    public void testSynchronizeWidth() {
        Node design = createDesign("width", "12px");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals(12, component.getWidth(), 0.1f);
        assertEquals(com.vaadin.server.Sizeable.Unit.PIXELS,
                component.getWidthUnits());
    }

    public void testSynchronizeHeight() {
        Node design = createDesign("height", "12px");
        AbstractComponent component = getComponent();
        component.synchronizeFromDesign(design, ctx);
        assertEquals(12, component.getHeight(), 0.1f);
        assertEquals(com.vaadin.server.Sizeable.Unit.PIXELS,
                component.getHeightUnits());

    }

    private AbstractComponent getComponent() {
        return new Label();
    }

    private Node createDesign(String key, String value) {
        Attributes attributes = new Attributes();
        attributes.put(key, value);
        Element node = new Element(Tag.valueOf("v-label"), "", attributes);
        return node;
    }
}
