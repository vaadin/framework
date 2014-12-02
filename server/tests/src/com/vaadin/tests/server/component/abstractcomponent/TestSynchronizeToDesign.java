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

import java.io.File;
import java.util.Locale;

import junit.framework.TestCase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;

import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test case for reading the attributes of the abstract component from design
 * 
 * @author Vaadin Ltd
 */
public class TestSynchronizeToDesign extends TestCase {

    private DesignContext ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new DesignContext();
    }

    public void testSynchronizeId() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setId("testId");
        component.synchronizeToDesign(design, ctx);
        // we only changed one of the attributes, others are at default values
        assertEquals(1, design.attributes().size());
        assertEquals("testId", design.attr("id"));
    }

    public void testSynchronizePrimaryStyleName() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setPrimaryStyleName("test-style");
        component.synchronizeToDesign(design, ctx);
        // we only changed one of the attributes, others are at default values
        assertEquals(1, design.attributes().size());
        assertEquals("test-style", design.attr("primary-style-name"));
    }

    public void testSynchronizeCaption() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setCaption("test-caption");
        component.synchronizeToDesign(design, ctx);
        // we only changed one of the attributes, others are at default values
        assertEquals(1, design.attributes().size());
        assertEquals("test-caption", design.attr("caption"));
    }

    public void testSynchronizeLocale() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setLocale(new Locale("fi", "FI"));
        component.synchronizeToDesign(design, ctx);
        // we only changed one of the attributes, others are at default values
        assertEquals(1, design.attributes().size());
        assertEquals("fi_FI", design.attr("locale"));
    }

    public void testSynchronizeExternalIcon() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component
                .setIcon(new ExternalResource("http://example.com/example.gif"));
        component.synchronizeToDesign(design, ctx);
        // we only changed one of the attributes, others are at default values
        assertEquals(1, design.attributes().size());
        assertEquals("http://example.com/example.gif", design.attr("icon"));
    }

    public void testSynchronizeThemeIcon() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setIcon(new ThemeResource("example.gif"));
        component.synchronizeToDesign(design, ctx);
        // we only changed one of the attributes, others are at default values
        assertEquals(1, design.attributes().size());
        assertEquals("theme://example.gif", design.attr("icon"));
    }

    public void testSynchronizeFileResource() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setIcon(new FileResource(new File("img/example.gif")));
        component.synchronizeToDesign(design, ctx);
        // we only changed one of the attributes, others are at default values
        assertEquals(1, design.attributes().size());
        assertEquals("img/example.gif", design.attr("icon"));
    }

    public void testSynchronizeImmediate() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setImmediate(true);
        component.synchronizeToDesign(design, ctx);
        // we only changed one of the attributes, others are at default values
        assertEquals(1, design.attributes().size());
        assertEquals("true", design.attr("immediate"));
    }

    public void testSynchronizeDescription() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setDescription("test-description");
        component.synchronizeToDesign(design, ctx);
        // we only changed one of the attributes, others are at default values
        assertEquals(1, design.attributes().size());
        assertEquals("test-description", design.attr("description"));
    }

    public void testSynchronizeComponentError() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setComponentError(new UserError("<div>test-error</div>",
                ContentMode.HTML, ErrorLevel.ERROR));
        component.synchronizeToDesign(design, ctx);
        // we only changed one of the attributes, others are at default values
        assertEquals(1, design.attributes().size());
        assertEquals("<div>test-error</div>", design.attr("error"));
    }

    public void testSynchronizeSizeFull() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setSizeFull();
        component.synchronizeToDesign(design, ctx);
        // there should be only size full
        assertEquals(1, design.attributes().size());
        assertEquals("true", design.attr("size-full"));
    }

    public void testSynchronizeSizeAuto() {
        Node design = createDesign();
        AbstractComponent component = getPanel();
        component.setSizeUndefined();
        component.synchronizeToDesign(design, ctx);
        // there should be only size auto
        assertEquals(1, design.attributes().size());
        assertEquals("true", design.attr("size-auto"));
    }

    public void testSynchronizeHeightFull() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setHeight("100%");
        component.setWidth("20px");
        component.synchronizeToDesign(design, ctx);
        assertEquals("true", design.attr("height-full"));
    }

    public void testSynchronizeHeightAuto() {
        Node design = createDesign();
        // we need to have default height of 100% -> use split panel
        AbstractComponent component = getPanel();
        component.setHeight(null);
        component.setWidth("20px");
        component.synchronizeToDesign(design, ctx);
        assertEquals("true", design.attr("height-auto"));
    }

    public void testSynchronizeWidthFull() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setHeight("20px");
        component.setWidth("100%");
        component.synchronizeToDesign(design, ctx);
        assertEquals("true", design.attr("width-full"));
    }

    public void testSynchronizeWidthAuto() {
        Node design = createDesign();
        // need to get label, otherwise the default would be auto
        AbstractComponent component = getPanel();
        component.setHeight("20px");
        component.setWidth(null);
        component.synchronizeToDesign(design, ctx);
        assertEquals("true", design.attr("width-auto"));
    }

    public void testSynchronizeWidth() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setHeight("20px");
        component.setWidth("70%");
        component.synchronizeToDesign(design, ctx);
        assertEquals("70%", design.attr("width"));
    }

    public void testSynchronizeHeight() {
        Node design = createDesign();
        AbstractComponent component = getComponent();
        component.setHeight("20px");
        component.setWidth("70%");
        component.synchronizeToDesign(design, ctx);
        assertEquals("20px", design.attr("height"));
    }

    private AbstractComponent getComponent() {
        return new Button();
    }

    private AbstractComponent getPanel() {
        return new HorizontalSplitPanel();
    }

    private Node createDesign() {
        Attributes attr = new Attributes();
        attr.put("should_be_removed", "foo");
        Element node = new Element(Tag.valueOf("v-button"), "", attr);
        Element child = new Element(Tag.valueOf("to-be-removed"), "foo", attr);
        node.appendChild(child);
        return node;
    }
}
