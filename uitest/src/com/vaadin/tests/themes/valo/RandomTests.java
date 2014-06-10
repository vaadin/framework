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
package com.vaadin.tests.themes.valo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class RandomTests extends VerticalLayout implements View {
    public RandomTests() {
        setMargin(true);

        Label h1 = new Label("Random Tests");
        h1.addStyleName("h1");
        addComponent(h1);

        Panel p = new Panel();
        addComponent(p);

        HorizontalLayout row = new HorizontalLayout();
        p.setHeight("300px");
        // row.setSpacing(true);
        // row.setMargin(true);
        row.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
        row.setWidth("100%");
        // row.setHeight("248px");
        row.setSizeFull();

        Button button = new Button("Boo");
        button.setWidth("100%");
        Button button2 = new Button("Abc");
        button2.setWidth("100%");
        row.addComponents(button, new InlineDateField(), new CheckBox("Far"));

        p.setContent(row);

        p = new Panel();
        addComponent(p);
        p.setHeight("400px");

        VerticalLayout col = new VerticalLayout();
        col.setSizeFull();
        col.setSpacing(true);
        // row.setMargin(true);
        col.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);

        col.addComponents(new Button("Boo"), new InlineDateField(),
                new CheckBox("Far"), new Button("Abc"));

        p.setContent(col);

        Table t = Tables.getTable(null);
        t.setSizeFull();
        p.setContent(t);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub
    }

}
