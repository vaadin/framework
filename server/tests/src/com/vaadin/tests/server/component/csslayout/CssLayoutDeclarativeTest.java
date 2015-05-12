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
package com.vaadin.tests.server.component.csslayout;

import org.junit.Test;

import com.vaadin.shared.ui.label.ContentMode;
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
        String design = "<v-css-layout />";
        CssLayout layout = new CssLayout();
        testRead(design, layout);
        testWrite(design, layout);
        design = "<v-css-layout caption=\"A caption\"/>";
        layout = new CssLayout();
        layout.setCaption("A caption");
        testRead(design, layout);
        testWrite(design, layout);
    }

    @Test
    public void testFeatures() {
        String design = "<v-css-layout caption=test-layout><v-label caption=test-label />"
                + "<v-button>test-button</v-button></v-css-layout>";
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
