package com.vaadin.tests.server.component.absolutelayout;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;

/**
 * Tests declarative support for implementations of {@link AbsoluteLayout}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class AbsoluteLayoutDeclarativeTest
        extends DeclarativeTestBase<AbsoluteLayout> {

    @Test
    public void testAbsoluteLayoutFeatures() {
        String design = "<vaadin-absolute-layout caption=\"test-layout\">"
                + "<vaadin-button :top='100px' :left='0px' :z-index=21>OK</vaadin-button>"
                + "<vaadin-button :bottom='0px' :right='0px'>Cancel</vaadin-button>"
                + "</vaadin-absolute-layout>";
        AbsoluteLayout layout = new AbsoluteLayout();
        layout.setCaption("test-layout");
        Button b1 = new Button("OK");
        b1.setCaptionAsHtml(true);
        Button b2 = new Button("Cancel");
        b2.setCaptionAsHtml(true);
        layout.addComponent(b1, "top: 100px; left: 0px; z-index: 21");
        layout.addComponent(b2, "bottom: 0px; right: 0px;");

        testWrite(design, layout);
        testRead(design, layout);
    }

    @Test
    public void testEmpty() {
        String design = "<vaadin-absolute-layout/>";
        AbsoluteLayout layout = new AbsoluteLayout();
        testRead(design, layout);
        testWrite(design, layout);
    }

}
