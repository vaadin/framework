package com.vaadin.tests.server.component.csslayout;

import org.junit.Test;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/**
 * Tests declarative support for CssLayout.
 *
 * @since
 * @author Vaadin Ltd
 */
public class CssLayoutDeclarativeTest extends DeclarativeTestBase<CssLayout> {

    @Test
    public void testNoChildren() {
        String design = "<vaadin-css-layout />";
        CssLayout layout = new CssLayout();
        testRead(design, layout);
        testWrite(design, layout);
        design = "<vaadin-css-layout caption=\"A caption\"/>";
        layout = new CssLayout();
        layout.setCaption("A caption");
        testRead(design, layout);
        testWrite(design, layout);
    }

    @Test
    public void testFeatures() {
        String design = "<vaadin-css-layout caption=test-layout><vaadin-label caption=test-label />"
                + "<vaadin-button>test-button</vaadin-button></vaadin-css-layout>";
        CssLayout layout = new CssLayout();
        layout.setCaption("test-layout");
        Label l = new Label();
        l.setContentMode(ContentMode.HTML);
        l.setCaption("test-label");
        layout.addComponent(l);
        Button b = new Button("test-button");
        b.setCaptionAsHtml(true);
        layout.addComponent(b);
        testRead(design, layout);
        testWrite(design, layout);
    }
}
