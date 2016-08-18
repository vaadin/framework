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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.legacy.data.validator.LegacyIntegerRangeValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.renderers.DateRenderer;

@Theme("valo")
public class GridThemeUI extends AbstractTestUIWithLog {

    private Grid grid;

    protected static String[] columns = new String[] { "firstName", "lastName",
            "gender", "birthDate", "age", "alive", "address.streetAddress",
            "address.postalCode", "address.city", "address.country" };

    protected BeanItemContainer<ComplexPerson> container = ComplexPerson
            .createContainer(100);;
    {
        container.addNestedContainerBean("address");
    }
    protected ComboBox formType;

    private Component active = null;

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(new Locale("en", "US"));

        final NativeSelect pageSelect = new NativeSelect("Page");
        pageSelect.setImmediate(true);
        pageSelect.setId("page");
        addComponent(pageSelect);

        pageSelect.addItem(new Editor());
        pageSelect.addItem(new HeaderFooter());

        pageSelect.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (active != null) {
                    removeComponent(active);
                }
                active = (Component) pageSelect.getValue();
                addComponent(active);
            }
        });
        pageSelect.setNullSelectionAllowed(false);
        pageSelect.setValue(pageSelect.getItemIds().iterator().next());

    }

    public class Editor extends Grid {
        @Override
        public String toString() {
            return "Editor";
        };

        public Editor() {
            setContainerDataSource(container);
            setColumnOrder((Object[]) columns);
            removeColumn("salary");
            setEditorEnabled(true);
            getColumn("lastName").setEditable(false);
            setSizeFull();
            getColumn("age").getEditorField().addValidator(
                    new LegacyIntegerRangeValidator("Must be between 0 and 100",
                            0, 100));
            getColumn("birthDate").setRenderer(new DateRenderer(
                    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US)));
        }
    }

    public class HeaderFooter extends Grid {
        @Override
        public String toString() {
            return getClass().getSimpleName();
        };

        public HeaderFooter() {
            setContainerDataSource(container);
            setColumnOrder((Object[]) columns);
            HeaderRow row = addHeaderRowAt(0);
            row.join("firstName", "lastName").setHtml("<b>Name</b>");
            Button b = new Button("The address, yo");
            b.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    HeaderRow row = addHeaderRowAt(0);
                    List<Object> pids = new ArrayList<Object>();
                    for (Column c : getColumns()) {
                        pids.add(c.getPropertyId());
                    }
                    row.join(pids.toArray()).setText("The big header");
                }
            });
            b.setSizeFull();
            row.join("address.streetAddress", "address.postalCode",
                    "address.city", "address.country").setComponent(b);
            // TODO: revert back to 25 when #16597 is fixed..
            getColumn("age").setWidth(42);
            removeColumn("salary");
            setEditorEnabled(true);
            setSizeFull();
            getColumn("age").getEditorField().addValidator(
                    new LegacyIntegerRangeValidator("Must be between 0 and 100",
                            0, 100));
            getColumn("birthDate").setRenderer(new DateRenderer(
                    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US)));

            addFooterRowAt(0);
        }
    }

}
