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
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.TestDataGenerator;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridDragSource;
import com.vaadin.ui.GridDropTarget;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.RadioButtonGroup;

import elemental.json.Json;
import elemental.json.JsonObject;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridDragAndDrop extends AbstractTestUIWithLog {

    private Set<Person> draggedItems;

    @Override
    protected void setup(VaadinRequest request) {
        getUI().setMobileHtml5DndEnabled(true);

        // Drag source Grid
        Grid<Person> left = createGridAndFillWithData(50);
        GridDragSource<Person> dragSource = applyDragSource(left);

        // Drop target Grid
        Grid<Person> right = createGridAndFillWithData(5);
        GridDropTarget<Person> dropTarget = applyDropTarget(right);

        // Layout the two grids
        Layout grids = new HorizontalLayout();
        grids.addComponents(left, right);

        // Selection modes
        List<Grid.SelectionMode> selectionModes = Arrays
                .asList(Grid.SelectionMode.SINGLE, Grid.SelectionMode.MULTI);
        RadioButtonGroup<Grid.SelectionMode> selectionModeSelect = new RadioButtonGroup<>(
                "Selection mode", selectionModes);
        selectionModeSelect.setSelectedItem(Grid.SelectionMode.SINGLE);
        selectionModeSelect.addValueChangeListener(
                event -> left.setSelectionMode(event.getValue()));

        // Drop locations
        List<DropMode> dropLocations = Arrays.asList(DropMode.values());
        RadioButtonGroup<DropMode> dropLocationSelect = new RadioButtonGroup<>(
                "Allowed drop location", dropLocations);
        dropLocationSelect.setSelectedItem(DropMode.BETWEEN);
        dropLocationSelect.addValueChangeListener(
                event -> dropTarget.setDropMode(event.getValue()));

        Layout controls = new HorizontalLayout(selectionModeSelect,
                dropLocationSelect);

        addComponents(controls, grids);
    }

    private Grid<Person> createGridAndFillWithData(int numberOfItems) {
        Grid<Person> grid = new Grid<>();

        grid.setItems(generateItems(numberOfItems));
        grid.addColumn(
                person -> person.getFirstName() + " " + person.getLastName())
                .setCaption("Name");
        grid.addColumn(person -> person.getAddress().getStreetAddress())
                .setCaption("Street Address");
        grid.addColumn(person -> person.getAddress().getCity())
                .setCaption("City");

        return grid;
    }

    private GridDragSource<Person> applyDragSource(Grid<Person> grid) {
        GridDragSource<Person> dragSource = new GridDragSource<>(grid);

        dragSource.setEffectAllowed(EffectAllowed.MOVE);

        // Set data generator
        dragSource.setDragDataGenerator(person -> {
            JsonObject data = Json.createObject();
            data.put("name",
                    person.getFirstName() + " " + person.getLastName());
            data.put("city", person.getAddress().getCity());
            return data;
        });

        // Add drag start listener
        dragSource.addGridDragStartListener(event -> {
            draggedItems = event.getDraggedItems();
            log("START: " + draggedItems.size() + ", :"
                    + draggedItems.stream().map(person -> person.getLastName())
                            .collect(Collectors.joining(" ")));
        });

        // Add drag end listener
        dragSource.addGridDragEndListener(event -> {
            if (event.getDropEffect() == DropEffect.MOVE) {
                // If drop is successful, remove dragged item from source Grid
                ((ListDataProvider<Person>) grid.getDataProvider()).getItems()
                        .removeAll(draggedItems);
                grid.getDataProvider().refreshAll();
                log("END: dropEffect=" + event.getDropEffect());
                // Remove reference to dragged items
                draggedItems = null;
            }
        });

        return dragSource;
    }

    private GridDropTarget<Person> applyDropTarget(Grid<Person> grid) {
        // Create and attach extension
        GridDropTarget<Person> dropTarget = new GridDropTarget<>(grid,
                DropMode.BETWEEN);
        dropTarget.setDropEffect(DropEffect.MOVE);

        // Add listener
        dropTarget.addGridDropListener(event -> {
            event.getDragSourceExtension().ifPresent(source -> {
                if (source instanceof GridDragSource) {
                    ListDataProvider<Person> dataProvider = (ListDataProvider<Person>) event
                            .getComponent().getDataProvider();
                    List<Person> items = (List<Person>) dataProvider.getItems();

                    // Calculate the target row's index
                    int index = items.indexOf(event.getDropTargetRow())
                            + (event.getDropLocation() == DropLocation.BELOW ? 1
                                    : 0);

                    // Add dragged items to the target Grid
                    items.addAll(index, draggedItems);
                    dataProvider.refreshAll();

                    log("DROP: dragData=" + event.getDataTransferText()
                            + ", target="
                            + event.getDropTargetRow().getFirstName() + " "
                            + event.getDropTargetRow().getLastName()
                            + ", location=" + event.getDropLocation());
                }
            });
        });

        return dropTarget;
    }

    private List<Person> generateItems(int num) {
        return Stream.generate(() -> generateRandomPerson(new Random()))
                .limit(num).collect(Collectors.toList());
    }

    private Person generateRandomPerson(Random r) {
        return new Person(TestDataGenerator.getFirstName(r),
                TestDataGenerator.getLastName(r), "foo@bar.com",
                TestDataGenerator.getPhoneNumber(r),
                TestDataGenerator.getStreetAddress(r),
                TestDataGenerator.getPostalCode(r),
                TestDataGenerator.getCity(r));
    }
}
