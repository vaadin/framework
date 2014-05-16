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

package com.vaadin.tests;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TestForAlignments extends CustomComponent {

    public TestForAlignments() {

        final VerticalLayout main = new VerticalLayout();

        final Button b1 = new Button("Right");
        final Button b2 = new Button("Left");
        final Button b3 = new Button("Bottom");
        final Button b4 = new Button("Top");
        final TextField t1 = new TextField("Right aligned");
        final TextField t2 = new TextField("Bottom aligned");
        final DateField d1 = new DateField("Center aligned");
        final DateField d2 = new DateField("Center aligned");

        final VerticalLayout vert = new VerticalLayout();
        vert.addComponent(b1);
        vert.addComponent(b2);
        vert.addComponent(t1);
        vert.addComponent(d1);
        // vert.getSize().setWidth(500);
        vert.setComponentAlignment(b1, Alignment.TOP_RIGHT);
        vert.setComponentAlignment(b2, Alignment.TOP_LEFT);
        vert.setComponentAlignment(t1, Alignment.TOP_RIGHT);
        vert.setComponentAlignment(d1, Alignment.TOP_CENTER);
        final HorizontalLayout hori = new HorizontalLayout();
        hori.addComponent(b3);
        hori.addComponent(b4);
        hori.addComponent(t2);
        hori.addComponent(d2);
        // hori.getSize().setHeight(200);
        hori.setComponentAlignment(b3, Alignment.BOTTOM_LEFT);
        hori.setComponentAlignment(b4, Alignment.TOP_LEFT);
        hori.setComponentAlignment(t2, Alignment.BOTTOM_LEFT);
        hori.setComponentAlignment(d2, Alignment.MIDDLE_LEFT);

        main.addComponent(vert);
        main.addComponent(hori);

        setCompositionRoot(main);

    }

}
