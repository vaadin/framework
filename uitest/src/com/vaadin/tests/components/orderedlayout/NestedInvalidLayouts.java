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
package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class NestedInvalidLayouts extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        fullWidthTest(getLayout(), null);
        fullWidthTest(getLayout(), "100%");
        fullHeightTest(getLayout(), null);
        fullHeightTest(getLayout(), "100%");
    }

    private void fullWidthTest(VerticalLayout layout, String rootWidth) {
        // Contains
        // HL (-1)
        // * VL (100%)
        // ** Button (-1) (wide)
        // ** Button (100%)

        // This should be rendered just as if VL width was -1 (which it will
        // become when sending width to client), i.e. both buttons should be
        // equally wide

        final VerticalLayout l = new VerticalLayout();
        l.setWidth(rootWidth);
        final Button c = new Button("blaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        c.setWidth(null);
        l.addComponent(c);
        final Button b = new Button("c");
        b.setWidth("100%");
        l.addComponent(b);
        layout.addComponent(new HorizontalLayout(l));
    }

    private void fullHeightTest(VerticalLayout layout, String rootHeight) {
        // Contains (height)
        // VL (-1)
        // * HL (100%)
        // ** Button (200px) (high)
        // ** Button (100%)

        // This should be rendered just as if HL height was -1 (which it will
        // become when sending height to client), i.e. both buttons should be
        // equally high

        final HorizontalLayout l = new HorizontalLayout();
        l.setHeight(rootHeight);

        final NativeButton c = new NativeButton("hiiiigh");
        c.setWidth(null);
        c.setHeight("200px");
        l.addComponent(c);
        final NativeButton b = new NativeButton("c");
        b.setHeight("100%");
        l.addComponent(b);
        VerticalLayout vl = new VerticalLayout(l);
        vl.setHeight("100%");
        layout.addComponent(vl);
    }

}
