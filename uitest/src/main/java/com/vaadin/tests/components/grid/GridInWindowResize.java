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
package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Theme("valo")
public class GridInWindowResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid g = new Grid();
        IndexedContainer cont = new IndexedContainer();
        for (int j = 0; j < 3; j++) {
            cont.addContainerProperty("" + j, String.class, "");
        }

        for (int k = 0; k < 100; k++) {
            Item addItem = cont.addItem(k);
            for (int j = 0; j < 3; j++) {
                addItem.getItemProperty("" + j).setValue(1 + "");
            }
        }
        g.setContainerDataSource(cont);
        g.setSizeFull();

        VerticalLayout vl = new VerticalLayout(g);
        vl.setSizeFull();
        Button resize = new Button("resize");
        VerticalLayout vl2 = new VerticalLayout(vl, resize);
        vl2.setSizeFull();

        final Window w = new Window(null, vl2);
        addWindow(w);

        w.center();
        w.setModal(true);
        w.setWidth("600px");
        w.setHeight("400px");

        resize.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                w.setWidth("400px");
            }
        });

    }
}
