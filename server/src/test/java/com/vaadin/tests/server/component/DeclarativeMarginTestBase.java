package com.vaadin.tests.server.component;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.MarginHandler;
import com.vaadin.ui.Layout.SpacingHandler;

public abstract class DeclarativeMarginTestBase<L extends Layout & MarginHandler & SpacingHandler>
        extends DeclarativeTestBase<L> {

    protected void testSpacing(String componentTag, boolean defaultSpacing) {
        // Spacing on
        String design = "<" + componentTag;
        if (!defaultSpacing) {
            design += " spacing";
        }
        design += " />";
        L layout = read(design);
        assertTrue(layout.isSpacing());
        testWrite(design, layout);

        // Spacing off
        design = "<" + componentTag;
        if (defaultSpacing) {
            design += " spacing='false'";
        }
        design += " />";
        layout = read(design);
        assertFalse(layout.isSpacing());
        testWrite(design, layout);
    }

    protected void testMargins(String componentTag, MarginInfo defaultMargin) {

        for (int i = 0; i < 16; ++i) {
            boolean top = (i & 1) == 1;
            boolean right = (i & 2) == 2;
            boolean bottom = (i & 4) == 4;
            boolean left = (i & 8) == 8;

            MarginInfo marginToTest = new MarginInfo(top, right, bottom, left);

            String design = getMarginTag(componentTag, defaultMargin,
                    marginToTest);

            // The assertEquals machinery in DeclarativeTestBase uses bean
            // introspection and MarginInfo is not a proper bean. It ends up
            // considering *all* MarginInfo objects equal... (#18229)
            L layout = read(design);
            assertEquals("For tag: " + design, marginToTest,
                    layout.getMargin());

            testWrite(design, layout);
        }
    }

    private String getMarginTag(String componentTag, MarginInfo defaultMargin,
            MarginInfo marginToTest) {
        String s = "<" + componentTag + " ";

        if (marginToTest.hasAll()) {
            if (!defaultMargin.hasAll()) {
                s += "margin ";
            }
        } else if (marginToTest.hasNone()) {
            if (!defaultMargin.hasNone()) {
                s += "margin=false ";
            }
        } else {
            if (marginToTest.hasLeft() != defaultMargin.hasLeft()) {
                s += marginValue("margin-left", defaultMargin.hasLeft());
            }
            if (marginToTest.hasRight() != defaultMargin.hasRight()) {
                s += marginValue("margin-right", defaultMargin.hasRight());
            }
            if (marginToTest.hasTop() != defaultMargin.hasTop()) {
                s += marginValue("margin-top", defaultMargin.hasTop());
            }
            if (marginToTest.hasBottom() != defaultMargin.hasBottom()) {
                s += marginValue("margin-bottom", defaultMargin.hasBottom());
            }
        }
        return s + " />";
    }

    private String marginValue(String prefix, boolean defaultOn) {
        return prefix + (defaultOn ? "=false " : " ");
    }
}
