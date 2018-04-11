package com.vaadin.tests.server.component.abstractcomponent;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test cases for reading and writing the properties of AbstractComponent.
 *
 * @author Vaadin Ltd
 */
public class AbstractComponentDeclarativeTest
        extends DeclarativeTestBase<AbstractComponent> {

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
                + "error=\"<div>test-error</div>\" />";
        component.setId("testId");
        component.setPrimaryStyleName("test-style");
        component.setCaption("test-caption");
        component.setLocale(new Locale("fi", "FI"));
        component.setDescription("test-description");
        component.setComponentError(new UserError("<div>test-error</div>",
                com.vaadin.server.AbstractErrorMessage.ContentMode.HTML,
                ErrorLevel.ERROR));
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
        boolean[] immediate = { true, false, true, true };
        for (String design1 : design) {
            component = (AbstractComponent) Design
                    .read(new ByteArrayInputStream(
                            design1.getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Test
    public void testExternalIcon() {
        String design = "<vaadin-label icon=\"http://example.com/example.gif\"/>";
        component.setIcon(
                new ExternalResource("http://example.com/example.gif"));
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
        component = new Panel();
        String design = "<vaadin-panel size-auto />";
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
        component = new Panel();
        String design = "<vaadin-panel height=\"20px\"/ width-auto />";
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
        assertEquals("Component should have only one extension", 1,
                component.getExtensions().size());
    }

    @Test
    public void testUnknownProperties() {
        String design = "<vaadin-label foo=\"bar\"/>";

        DesignContext context = readAndReturnContext(design);
        Label label = (Label) context.getRootComponent();
        assertTrue("Custom attribute was preserved in custom attributes",
                context.getCustomAttributes(label).containsKey("foo"));

        testWrite(label, design, context);
    }

    private Element createDesign(boolean responsive) {
        Attributes attributes = new Attributes();
        attributes.put("responsive", responsive);
        Element node = new Element(Tag.valueOf("vaadin-label"), "", attributes);
        return node;
    }

}
