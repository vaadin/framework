package com.vaadin.tests.server.component.abstractorderedlayout;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.server.component.DeclarativeMarginTestBase;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Tests declarative support for AbstractOrderedLayout.
 *
 * @author Vaadin Ltd
 */
public class AbstractOrderedLayoutDeclarativeTest
        extends DeclarativeMarginTestBase<AbstractOrderedLayout> {

    private List<String> defaultAlignments = Arrays.asList(":top", ":left");

    @Test
    public void testMarginsVertical() {
        testMargins("vaadin-vertical-layout", new MarginInfo(true));
    }

    @Test
    public void testMarginsHorizontal() {
        testMargins("vaadin-horizontal-layout", new MarginInfo(false));
    }

    @Test
    public void testMarginsForm() {
        testMargins("vaadin-form-layout", new MarginInfo(true, false));
    }

    @Test
    public void testSpacingVertical() {
        testSpacing("vaadin-vertical-layout", true);
    }

    @Test
    public void testSpacingHorizontal() {
        testSpacing("vaadin-horizontal-layout", true);
    }

    @Test
    public void testSpacingForm() {
        testSpacing("vaadin-form-layout", true);
    }

    @Test
    public void testExpandRatio() {
        String design = getDesign(1);
        AbstractOrderedLayout layout = getLayout(1, null);
        testRead(design, layout);
        testWrite(design, layout);
        design = getDesign(0.25f);
        layout = getLayout(0.25f, null);
        testRead(design, layout);
        testWrite(design, layout);
    }

    @Test
    public void testAlignment() {
        String design = getDesign(0, ":top", ":left");
        AbstractOrderedLayout layout = getLayout(0, Alignment.TOP_LEFT);
        testRead(design, layout);
        testWrite(design, layout);
        design = getDesign(0, ":middle", ":center");
        layout = getLayout(0, Alignment.MIDDLE_CENTER);
        testRead(design, layout);
        testWrite(design, layout);
        design = getDesign(0, ":bottom", ":right");
        layout = getLayout(0, Alignment.BOTTOM_RIGHT);
        testRead(design, layout);
        testWrite(design, layout);
    }

    private String getDesign(float expandRatio, String... alignments) {
        String result = "<vaadin-vertical-layout caption=test-layout>";
        result += "<vaadin-label caption=test-label ";
        String ratioString = expandRatio == 1.0f ? null
                : String.valueOf(expandRatio);
        if (expandRatio != 0) {
            if (ratioString == null) {
                result += ":expand";
            } else {
                result += ":expand=" + ratioString;
            }
        }
        for (String alignment : alignments) {
            if (!defaultAlignments.contains(alignment)) {
                result += " " + alignment;
            }
        }
        result += "></vaadin-label><vaadin-button ";
        if (expandRatio != 0) {
            if (ratioString == null) {
                result += ":expand";
            } else {
                result += ":expand=" + ratioString;
            }
        }
        for (String alignment : alignments) {
            if (!defaultAlignments.contains(alignment)) {
                result += " " + alignment;
            }
        }
        result += "></vaadin-button></vaadin-vertical-layout>";
        return result;
    }

    private AbstractOrderedLayout getLayout(float expandRatio,
            Alignment alignment) {
        VerticalLayout layout = new VerticalLayout();
        layout.setCaption("test-layout");
        Label l = new Label();
        l.setCaption("test-label");
        l.setContentMode(ContentMode.HTML);
        layout.addComponent(l);
        layout.setExpandRatio(l, expandRatio);
        Button b = new Button();
        b.setCaptionAsHtml(true);
        layout.addComponent(b);
        layout.setExpandRatio(b, expandRatio);
        if (alignment != null) {
            layout.setComponentAlignment(l, alignment);
            layout.setComponentAlignment(b, alignment);
        }
        return layout;
    }
}
