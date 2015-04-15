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
public class AbsoluteLayoutDeclarativeTest extends
        DeclarativeTestBase<AbsoluteLayout> {

    @Test
    public void testAbsoluteLayoutFeatures() {
        String design = "<v-absolute-layout caption=\"test-layout\">"
                + "<v-button :top='100px' :left='0px' :z-index=21>OK</v-button>"
                + "<v-button :bottom='0px' :right='0px'>Cancel</v-button>"
                + "</v-absolute-layout>";
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
        String design = "<v-absolute-layout/>";
        AbsoluteLayout layout = new AbsoluteLayout();
        testRead(design, layout);
        testWrite(design, layout);
    }

}
