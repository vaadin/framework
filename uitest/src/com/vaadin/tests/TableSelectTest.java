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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TableSelectTest extends CustomComponent implements
        Table.ValueChangeListener {

    public TableSelectTest() {
        final VerticalLayout main = new VerticalLayout();
        setCompositionRoot(main);
        main.addComponent(new Label("Hello World!"));

        Table t;
        t = new Table("single nullsel");
        main.addComponent(t);
        t(t);
        t.setMultiSelect(false);
        t.setNullSelectionAllowed(true);
        t.addListener(this);

        t = new Table("single NO-nullsel");
        main.addComponent(t);
        t(t);
        t.setMultiSelect(false);
        t.setNullSelectionAllowed(false);
        t.addListener(this);

        t = new Table("multi nullsel");
        main.addComponent(t);
        t(t);
        t.setMultiSelect(true);
        t.setNullSelectionAllowed(true);
        t.addListener(this);

        t = new Table("multi NO-nullsel");
        main.addComponent(t);
        t(t);
        t.setMultiSelect(true);
        t.setNullSelectionAllowed(false);
        t.addListener(this);

        // --

        t = new Table("single nullsel nullselid");
        main.addComponent(t);
        Object id = t(t);
        t.setNullSelectionItemId(id);
        t.setMultiSelect(false);
        t.setNullSelectionAllowed(true);
        t.addListener(this);

        t = new Table("single NO-nullsel nullselid");
        main.addComponent(t);
        id = t(t);
        t.setNullSelectionItemId(id);
        t.setMultiSelect(false);
        t.setNullSelectionAllowed(false);
        t.addListener(this);

        t = new Table("multi(fails) nullsel nullselid");
        main.addComponent(t);
        id = t(t);
        t.setNullSelectionItemId(id);
        try {
            t.setMultiSelect(true);
            t.setCaption("multi(SHOLD FAIL BUT DID NOT) nullsel nullselid");
        } catch (final Exception e) {
            System.err.println("failed ok");
        }
        t.setNullSelectionAllowed(true);
        t.addListener(this);

        t = new Table("multi(fails) NO-nullsel nullselid");
        main.addComponent(t);
        id = t(t);
        t.setNullSelectionItemId(id);
        try {
            t.setMultiSelect(true);
            t.setCaption("multi(SHOLD FAIL BUT DID NOT) NO-nullsel nullselid");
        } catch (final Exception e) {
            System.err.println("failed ok");
        }
        t.setNullSelectionAllowed(false);
        t.addListener(this);

        /*
         * And that's it! The framework will display the main window and its
         * contents when the application is accessed with the terminal.
         */

    }

    private Object t(Table t) {
        t.setImmediate(true);
        t.setSelectable(true);

        Object id = null;
        for (int i = 0; i < 5; i++) {
            id = t.addItem();
        }
        t.addContainerProperty("asd", String.class, "the asd thing");
        t.addContainerProperty("foo", String.class, "foo stuff");
        t.addContainerProperty("Alonger column header", String.class, "short");

        return id;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        final Object val = event.getProperty().getValue();

        System.err.println("Value: " + val);

    }

}
