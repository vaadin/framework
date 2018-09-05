package com.vaadin.tests.design;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

/**
 * A test for checking that parsing a layout preserves the IDs and the mapping
 * from prefixes to package names (for example
 * <meta name=”package-mapping” content=”my:com.addon.mypackage” />)
 *
 * @author Vaadin Ltd
 */
public class ParseLayoutTest {
    // The context is used for accessing the created component hierarchy.
    private DesignContext ctx;

    @Before
    public void setUp() throws Exception {
        ctx = Design.read(getClass().getResourceAsStream("testFile.html"),
                null);
    }

    @Test
    public void buttonWithIdIsParsed() {
        Component button = ctx.getComponentByLocalId("firstButton");

        assertThat(ctx.getComponentByCaption("Native click me"), is(button));
        assertThat(button.getCaption(), is("Native click me"));
    }

    @Test
    public void buttonWithIdAndLocalIdIsParsed() {
        Component button = ctx.getComponentById("secondButton");

        assertThat(ctx.getComponentByCaption("Another button"), is(button));
        assertThat(ctx.getComponentByLocalId("localID"), is(button));
        assertThat(button.getCaption(), is("Another button"));
    }

    @Test
    public void buttonWithoutIdsIsParsed() {
        assertThat(ctx.getComponentByCaption("Yet another button"),
                is(not(nullValue())));
    }

    @Test
    public void serializationPreservesProperties() throws IOException {
        ByteArrayOutputStream out = serializeDesign(ctx);
        ctx = deSerializeDesign(out);

        assertButtonProperties();
    }

    @Test
    public void serializationPreservesHierarchy() throws IOException {
        ByteArrayOutputStream out = serializeDesign(ctx);
        ctx = deSerializeDesign(out);

        assertComponentHierarchy();
    }

    @Test
    public void designIsSerializedWithCorrectPrefixesAndPackageNames()
            throws IOException {
        ByteArrayOutputStream out = serializeDesign(ctx);

        // Check the mapping from prefixes to package names using the html tree
        String[] expectedPrefixes = { "my" };
        String[] expectedPackageNames = { "com.addon.mypackage" };
        int index = 0;

        Document doc = Jsoup.parse(out.toString(UTF_8.name()));
        Element head = doc.head();
        for (Node child : head.childNodes()) {
            if ("meta".equals(child.nodeName())) {
                String name = child.attributes().get("name");
                if ("package-mapping".equals(name)) {
                    String content = child.attributes().get("content");
                    String[] parts = content.split(":");
                    assertEquals("Unexpected prefix.", expectedPrefixes[index],
                            parts[0]);
                    assertEquals("Unexpected package name.",
                            expectedPackageNames[index], parts[1]);
                    index++;
                }
            }
        }
        assertEquals("Unexpected number of prefix - package name pairs.", 1,
                index);
    }

    private DesignContext deSerializeDesign(ByteArrayOutputStream out) {
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return Design.read(in, null);
    }

    private ByteArrayOutputStream serializeDesign(DesignContext context)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Design.write(context, out);

        return out;
    }

    private void assertButtonProperties() {
        buttonWithIdAndLocalIdIsParsed();
        buttonWithIdIsParsed();
        buttonWithoutIdsIsParsed();
    }

    @Test
    public void fieldsAreBoundToATemplate() throws IOException {
        LayoutTemplate template = new LayoutTemplate();
        InputStream htmlFile = getClass().getResourceAsStream("testFile.html");
        Design.read(htmlFile, template);
        assertNotNull(template.getFirstButton());
        assertNotNull(template.getSecondButton());
        assertNotNull(template.getYetanotherbutton());
        assertNotNull(template.getClickme());
        assertEquals("Native click me", template.getFirstButton().getCaption());
    }

    @Test(expected = DesignException.class)
    public void fieldsCannotBeBoundToAnInvalidTemplate() throws IOException {
        InvalidLayoutTemplate template = new InvalidLayoutTemplate();
        InputStream htmlFile = getClass().getResourceAsStream("testFile.html");

        Design.read(htmlFile, template);
    }

    @Test
    public void rootHasCorrectComponents() {
        Component root = ctx.getRootComponent();

        VerticalLayout vlayout = (VerticalLayout) root;

        assertThat(vlayout.getComponentCount(), is(3));
    }

    @Test
    public void rootChildHasCorrectComponents() {
        Component root = ctx.getRootComponent();
        VerticalLayout vlayout = (VerticalLayout) root;
        HorizontalLayout hlayout = (HorizontalLayout) vlayout.getComponent(0);

        assertThat(hlayout.getComponentCount(), is(5));
        assertThat(hlayout.getComponent(0).getCaption(), is("FooBar"));
        assertThat(hlayout.getComponent(1).getCaption(), is("Native click me"));
        assertThat(hlayout.getComponent(2).getCaption(), is("Another button"));
        assertThat(hlayout.getComponent(3).getCaption(),
                is("Yet another button"));
        assertThat(hlayout.getComponent(4).getCaption(), is("Click me"));
        assertThat(hlayout.getComponent(4).getWidth(), is(150f));

        // Check the remaining two components of the vertical layout
        assertTextField(vlayout);
        assertPasswordField(vlayout);
    }

    private void assertComponentHierarchy() {
        rootHasCorrectComponents();
        rootChildHasCorrectComponents();
    }

    private void assertTextField(VerticalLayout vlayout) {
        TextField tf = (TextField) vlayout.getComponent(1);

        assertThat(tf.getCaption(), is("Text input"));
    }

    private void assertPasswordField(VerticalLayout layout) {
        PasswordField pf = (PasswordField) layout.getComponent(2);

        assertThat(pf.getCaption(), is("Password field"));
        assertThat(pf.getWidth(), is(300f));
        assertThat(pf.getHeight(), is(200f));
    }
}
