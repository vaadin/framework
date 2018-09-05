package com.vaadin.tests.design.nested;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import com.vaadin.tests.design.nested.customlayouts.CustomAbsoluteLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomAccordion;
import com.vaadin.tests.design.nested.customlayouts.CustomCssLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomFormLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomGridLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomHorizontalLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomHorizontalSplitPanel;
import com.vaadin.tests.design.nested.customlayouts.CustomPanel;
import com.vaadin.tests.design.nested.customlayouts.CustomTabSheet;
import com.vaadin.tests.design.nested.customlayouts.CustomVerticalLayout;
import com.vaadin.tests.design.nested.customlayouts.CustomVerticalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/**
 * Test case for nested custom layouts. The children of the custom layouts must
 * not be rendered.
 *
 * @author Vaadin Ltd
 */
public class NestedCustomLayoutsTest {

    private static final String PACKAGE_MAPPING = "com_vaadin_tests_design_nested_customlayouts:com.vaadin.tests.design.nested.customlayouts";

    @Test
    public void testNestedLayouts() throws IOException {
        VerticalLayout rootLayout = createRootLayout();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Design.write(rootLayout, out);
        Document doc = Jsoup.parse(out.toString(UTF_8.name()));

        assertThat(doc.head().child(0).attr("name"), is("package-mapping"));
        assertThat(doc.head().child(0).attr("content"), is(PACKAGE_MAPPING));
        assertChildrenCount(doc);
    }

    private VerticalLayout createRootLayout() {
        VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.addComponent(new CustomAbsoluteLayout());
        rootLayout.addComponent(new CustomAccordion());
        rootLayout.addComponent(new CustomCssLayout());
        rootLayout.addComponent(new CustomFormLayout());
        rootLayout.addComponent(new CustomGridLayout());
        rootLayout.addComponent(new CustomHorizontalLayout());
        rootLayout.addComponent(new CustomHorizontalSplitPanel());
        rootLayout.addComponent(new CustomPanel());
        rootLayout.addComponent(new CustomTabSheet());
        rootLayout.addComponent(new CustomVerticalLayout());
        rootLayout.addComponent(new CustomVerticalSplitPanel());

        return rootLayout;
    }

    private void assertChildrenCount(Document doc) {
        Element rootNode = doc.body().child(0);
        assertThat(rootNode.children().size(), greaterThan(0));

        for (Element child : rootNode.children()) {
            // make sure that the nested custom layouts do not render children
            assertThat(child.children().size(), is(0));
        }
    }
}
