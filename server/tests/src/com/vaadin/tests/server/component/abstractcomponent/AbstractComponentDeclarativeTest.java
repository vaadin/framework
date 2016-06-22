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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Locale;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test cases for reading and writing the properties of AbstractComponent.
 * 
 * @author Vaadin Ltd
 */
public class AbstractComponentDeclarativeTest extends
        DeclarativeTestBase<AbstractComponent> {

    private AbstractComponent component;

    @Before
    public void setUp() {
        Label l = new Label();
        l.setContentMode(ContentMode.HTML);
        component = l;
    }

    @Test
    public void testEmptyDesign() {
        String design = "<vaadin-label>";
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testProperties() {
        String design = "<vaadin-label id=\"testId\" primary-style-name=\"test-style\" "
                + "caption=\"test-caption\" locale=\"fi_FI\" description=\"test-description\" "
                + "error=\"<div>test-error</div>\" immediate />";
        component.setId("testId");
        component.setPrimaryStyleName("test-style");
        component.setCaption("test-caption");
        component.setLocale(new Locale("fi", "FI"));
        component.setDescription("test-description");
        component.setComponentError(new UserError("<div>test-error</div>",
                com.vaadin.server.AbstractErrorMessage.ContentMode.HTML,
                ErrorLevel.ERROR));
        component.setImmediate(true);
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testReadImmediate() {
        // Additional tests for the immediate property, including
        // explicit immediate values
        String[] design = { "<vaadin-label/>",
                "<vaadin-label immediate=\"false\"/>",
                "<vaadin-label immediate=\"true\"/>",
                "<vaadin-label immediate />" };
        Boolean[] explicitImmediate = { null, Boolean.FALSE, Boolean.TRUE,
                Boolean.TRUE };
        boolean[] immediate = { false, false, true, true };
        for (int i = 0; i < design.length; i++) {
            component = (AbstractComponent) Design
                    .read(new ByteArrayInputStream(design[i].getBytes(Charset
                            .forName("UTF-8"))));
            assertEquals(immediate[i], component.isImmediate());
            assertEquals(explicitImmediate[i], getExplicitImmediate(component));
        }
    }

    @Test
    public void testExternalIcon() {
        String design = "<vaadin-label icon=\"http://example.com/example.gif\"/>";
        component
                .setIcon(new ExternalResource("http://example.com/example.gif"));
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testThemeIcon() {
        String design = "<vaadin-label icon=\"theme://example.gif\"/>";
        component.setIcon(new ThemeResource("example.gif"));
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testFileResourceIcon() {
        String design = "<vaadin-label icon=\"img/example.gif\"/>";
        component.setIcon(new FileResource(new File("img/example.gif")));
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testWidthAndHeight() {
        String design = "<vaadin-label width=\"70%\" height=\"12px\"/>";
        component.setWidth("70%");
        component.setHeight("12px");
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testSizeFull() {
        String design = "<vaadin-label size-full />";
        component.setSizeFull();
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testSizeAuto() {
        String design = "<vaadin-label size-auto />";
        component.setSizeUndefined();
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testHeightFull() {
        String design = "<vaadin-label height-full width=\"20px\"/>";
        component.setHeight("100%");
        component.setWidth("20px");
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testHeightAuto() {
        String design = "<vaadin-horizontal-split-panel height-auto width=\"20px\" >";
        // we need to have default height of 100% -> use split panel
        AbstractComponent component = new HorizontalSplitPanel();
        component.setHeight(null);
        component.setWidth("20px");
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testWidthFull() {
        String design = "<vaadin-button width-full height=\"20px\">Foo</vaadin-button>";
        AbstractComponent component = new Button();
        component.setCaptionAsHtml(true);
        component.setCaption("Foo");
        component.setHeight("20px");
        component.setWidth("100%");
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testWidthAuto() {
        String design = "<vaadin-label height=\"20px\"/ width-auto />";
        component.setCaptionAsHtml(false);
        component.setHeight("20px");
        component.setWidth(null);
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testResponsive() {
        String design = "<vaadin-label responsive />";
        Responsive.makeResponsive(component);
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testResponsiveFalse() {
        String design = "<vaadin-label responsive =\"false\"/>";
        // Only test read as the attribute responsive=false would not be written
        testRead(design, component);
    }

    @Test
    public void testReadAlreadyResponsive() {
        AbstractComponent component = new Label();
        Responsive.makeResponsive(component);
        Element design = createDesign(true);
        component.readDesign(design, new DesignContext());
        assertEquals("Component should have only one extension", 1, component
                .getExtensions().size());
    }

    private Element createDesign(boolean responsive) {
        Attributes attributes = new Attributes();
        attributes.put("responsive", responsive);
        Element node = new Element(Tag.valueOf("vaadin-label"), "", attributes);
        return node;
    }

    private Boolean getExplicitImmediate(AbstractComponent component) {
        try {
            Field immediate = AbstractComponent.class
                    .getDeclaredField("explicitImmediateValue");
            immediate.setAccessible(true);
            return (Boolean) immediate.get(component);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Getting the field explicitImmediateValue failed.");
        }
    }
}
