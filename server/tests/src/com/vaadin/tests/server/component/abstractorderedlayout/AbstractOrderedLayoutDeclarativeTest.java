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
package com.vaadin.tests.server.component.abstractorderedlayout;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Tests declarative support for AbstractOrderedLayout.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class AbstractOrderedLayoutDeclarativeTest extends
        DeclarativeTestBase<AbstractOrderedLayout> {

    private List<String> defaultAlignments = Arrays.asList(new String[] {
            ":top", ":left" });

    @Test
    public void testMargin() {
        String design = getDesign(0, true);
        AbstractOrderedLayout layout = getLayout(0, true, null);
        testRead(design, layout);
        testWrite(design, layout);
        design = getDesign(0, false);
        layout = getLayout(0, false, null);
        testRead(design, layout);
        testWrite(design, layout);
    }

    @Test
    public void testExpandRatio() {
        String design = getDesign(1, false);
        AbstractOrderedLayout layout = getLayout(1, false, null);
        testRead(design, layout);
        testWrite(design, layout);
        design = getDesign(0.25f, false);
        layout = getLayout(0.25f, false, null);
        testRead(design, layout);
        testWrite(design, layout);
    }

    @Test
    public void testAlignment() {
        String design = getDesign(0, false, ":top", ":left");
        AbstractOrderedLayout layout = getLayout(0, false, Alignment.TOP_LEFT);
        testRead(design, layout);
        testWrite(design, layout);
        design = getDesign(0, false, ":middle", ":center");
        layout = getLayout(0, false, Alignment.MIDDLE_CENTER);
        testRead(design, layout);
        testWrite(design, layout);
        design = getDesign(0, false, ":bottom", ":right");
        layout = getLayout(0, false, Alignment.BOTTOM_RIGHT);
        testRead(design, layout);
        testWrite(design, layout);
    }

    private String getDesign(float expandRatio, boolean margin,
            String... alignments) {
        String result = "<v-vertical-layout caption=test-layout";
        if (margin) {
            result += " margin=true";
        }
        result += "><v-label caption=test-label ";
        String ratioString = expandRatio == 1.0f ? "\"\"" : String
                .valueOf(expandRatio);
        if (expandRatio != 0) {
            result += ":expand=" + ratioString;
        }
        for (String alignment : alignments) {
            if (!defaultAlignments.contains(alignment)) {
                result += " " + alignment + "=\"\"";
            }
        }
        result += "></v-label><v-button ";
        if (expandRatio != 0) {
            result += ":expand=" + ratioString;
        }
        for (String alignment : alignments) {
            if (!defaultAlignments.contains(alignment)) {
                result += " " + alignment + "=\"\"";
            }
        }
        result += "></v-button></v-vertical-layout>";
        return result;
    }

    private AbstractOrderedLayout getLayout(float expandRatio, boolean margin,
            Alignment alignment) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(margin);
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