package com.vaadin.tests.server.component.link;

import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Link;

/**
 * Test cases for reading the properties of selection components.
 *
 * @author Vaadin Ltd
 */
public class LinkDeclarativeTest extends DeclarativeTestBase<Link> {
    private String getBasicDesign() {
        return "<vaadin-link href='http://vaadin.com' target='vaadin-window' target-height=500"
                + " target-width=800 target-border='none' />";
    }

    private Link getBasicExpected() {
        Link l = new Link();
        l.setResource(new ExternalResource("http://vaadin.com"));
        l.setTargetName("vaadin-window");
        l.setTargetBorder(BorderStyle.NONE);
        l.setTargetHeight(500);
        l.setTargetWidth(800);
        return l;
    }

    @Test
    public void readBasic() throws Exception {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void writeBasic() throws Exception {
        testWrite(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testReadEmpty() {
        testRead("<vaadin-link />", new Link());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<vaadin-link />", new Link());
    }

}