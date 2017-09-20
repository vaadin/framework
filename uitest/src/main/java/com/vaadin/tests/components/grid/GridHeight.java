/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.NumberRenderer;

/**
 * Tests that Grid gets correct height based on height mode, and resizes
 * properly with details row if height is undefined.
 *
 * @author Vaadin Ltd
 */
public class GridHeight extends AbstractReindeerTestUI {

    static final String FULL = "Full";
    static final String UNDEFINED = "Undefined";
    static final String PX100 = "100px";
    static final Integer ROW3 = 3;

    static final Object[] gridHeights = { FULL, UNDEFINED, ROW3 };
    static final String[] gridWidths = { FULL, UNDEFINED };
    static final String[] detailsRowHeights = { FULL, UNDEFINED, PX100 };

    private Grid<Person> grid;
    private Map<Person, VerticalLayout> detailsLayouts = new HashMap<>();
    private RadioButtonGroup<String> detailsHeightSelector;

    @Override
    protected void setup(VaadinRequest request) {

        grid = new Grid<>();

        grid.addColumn(Person::getFirstName);
        grid.addColumn(Person::getAge, new NumberRenderer());

        grid.setItems(createPersons());

        grid.setDetailsGenerator(person -> {
            if (!detailsLayouts.containsKey(person)) {
                createDetailsLayout(person);
            }
            return detailsLayouts.get(person);
        });

        grid.addItemClickListener(click -> grid.setDetailsVisible(
                click.getItem(), !grid.isDetailsVisible(click.getItem())));

        addComponent(createOptionLayout());
        addComponent(grid);
    }

    private List<Person> createPersons() {
        Person person1 = new Person();
        person1.setFirstName("Nicolaus Copernicus");
        person1.setAge(1543);

        Person person2 = new Person();
        person2.setFirstName("Galileo Galilei");
        person2.setAge(1564);

        Person person3 = new Person();
        person3.setFirstName("Johannes Kepler");
        person3.setAge(1571);

        return Arrays.asList(person1, person2, person3);
    }

    private void createDetailsLayout(Person person) {
        VerticalLayout detailsLayout = new VerticalLayout();
        setDetailsHeight(detailsLayout, detailsHeightSelector.getValue());
        detailsLayout.setWidth("100%");

        Label lbl1 = new Label("details row");
        lbl1.setId("lbl1");
        lbl1.setSizeUndefined();
        detailsLayout.addComponent(lbl1);
        detailsLayout.setComponentAlignment(lbl1, Alignment.MIDDLE_CENTER);

        detailsLayouts.put(person, detailsLayout);
    }

    private Component createOptionLayout() {
        HorizontalLayout optionLayout = new HorizontalLayout();
        RadioButtonGroup<Object> gridHeightSelector = new RadioButtonGroup<>(
                "Grid height");
        gridHeightSelector.setItems(Arrays.asList(gridHeights));
        gridHeightSelector.setId("gridHeightSelector");

        gridHeightSelector.setItemCaptionGenerator(this::generateCaption);

        gridHeightSelector.addValueChangeListener(event -> {
            Object value = event.getValue();
            if (UNDEFINED.equals(value)) {
                grid.setHeightUndefined();
                grid.setHeightMode(HeightMode.UNDEFINED);
            } else if (FULL.equals(value)) {
                grid.setHeight("100%");
                grid.setHeightMode(HeightMode.CSS);
            } else if (ROW3.equals(value)) {
                grid.setHeightByRows(ROW3);
                grid.setHeightMode(HeightMode.ROW);
            }
        });
        gridHeightSelector.setValue(UNDEFINED);
        optionLayout.addComponent(gridHeightSelector);

        RadioButtonGroup<String> gridWidthSelector = new RadioButtonGroup<>(
                "Grid width", Arrays.asList(gridWidths));
        gridWidthSelector.setId("gridWidthSelector");
        gridWidthSelector.addValueChangeListener(event -> {
            Object value = event.getValue();
            if (UNDEFINED.equals(value)) {
                grid.setWidthUndefined();
            } else if (FULL.equals(value)) {
                grid.setWidth("100%");
            }
        });
        gridWidthSelector.setValue(UNDEFINED);
        optionLayout.addComponent(gridWidthSelector);

        detailsHeightSelector = new RadioButtonGroup<>("Details row height");
        detailsHeightSelector.setItems(Arrays.asList(detailsRowHeights));
        detailsHeightSelector.setId("detailsHeightSelector");
        detailsHeightSelector.addValueChangeListener(event -> {
            Object value = event.getValue();
            for (VerticalLayout detailsLayout : detailsLayouts.values()) {
                setDetailsHeight(detailsLayout, value);
            }
        });
        detailsHeightSelector.setValue(PX100);
        optionLayout.addComponent(detailsHeightSelector);
        return optionLayout;
    }

    private void setDetailsHeight(VerticalLayout detailsLayout, Object value) {
        if (UNDEFINED.equals(value)) {
            detailsLayout.setHeightUndefined();
        } else if (FULL.equals(value)) {
            detailsLayout.setHeight("100%");
        } else if (PX100.equals(value)) {
            detailsLayout.setHeight(PX100);
        }
    }

    @Override
    protected String getTestDescription() {
        return "Grid with undefined height should display all rows and resize when details row is opened."
                + "<br>Grid with full height is always 400px high regardless or details row."
                + "<br>Grid with row height should always be the height of those rows regardless of details row.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19690;
    }

    private String generateCaption(Object item) {
        if (item instanceof String) {
            return item.toString();
        } else {
            return item + " rows";
        }
    }
}
