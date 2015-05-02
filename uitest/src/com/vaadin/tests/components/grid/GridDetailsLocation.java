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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.DetailsGenerator;
import com.vaadin.ui.Grid.RowReference;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Theme(ValoTheme.THEME_NAME)
public class GridDetailsLocation extends UI {

    private final DetailsGenerator detailsGenerator = new DetailsGenerator() {
        @Override
        public Component getDetails(RowReference rowReference) {
            Person person = (Person) rowReference.getItemId();
            Label label = new Label(person.getFirstName() + " "
                    + person.getLastName());
            // currently the decorator row doesn't change its height when the
            // content height is different.
            label.setHeight("30px");
            return label;
        }
    };

    private TextField numberTextField;
    private Grid grid;

    @Override
    protected void init(VaadinRequest request) {

        Layout layout = new VerticalLayout();

        grid = new Grid(PersonContainer.createWithTestData(1000));
        grid.setSelectionMode(SelectionMode.NONE);
        layout.addComponent(grid);

        final CheckBox checkbox = new CheckBox("Details generator");
        checkbox.addValueChangeListener(new ValueChangeListener() {
            @Override
            @SuppressWarnings("boxing")
            public void valueChange(ValueChangeEvent event) {
                if (checkbox.getValue()) {
                    grid.setDetailsGenerator(detailsGenerator);
                } else {
                    grid.setDetailsGenerator(DetailsGenerator.NULL);
                }
            }
        });
        layout.addComponent(checkbox);

        numberTextField = new TextField("Row");
        numberTextField.setImmediate(true);
        layout.addComponent(numberTextField);

        layout.addComponent(new Button("Toggle and scroll",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        toggle();
                        scrollTo();
                    }
                }));
        layout.addComponent(new Button("Scroll and toggle",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        scrollTo();
                        toggle();
                    }
                }));

        setContent(layout);
    }

    private void toggle() {
        Object itemId = getItemId();
        boolean isVisible = grid.isDetailsVisible(itemId);
        grid.setDetailsVisible(itemId, !isVisible);
    }

    private void scrollTo() {
        grid.scrollTo(getItemId());
    }

    private Object getItemId() {
        int row = Integer.parseInt(numberTextField.getValue());
        Object itemId = grid.getContainerDataSource().getIdByIndex(row);
        return itemId;
    }

}
