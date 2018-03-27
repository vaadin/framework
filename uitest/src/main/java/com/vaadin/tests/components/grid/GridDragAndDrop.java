/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridDragSource;
import com.vaadin.ui.components.grid.GridDropTarget;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridDragAndDrop extends AbstractGridDnD {

    private List<Person> draggedItems;

    @Override
    protected void setup(VaadinRequest request) {
        getUI().setMobileHtml5DndEnabled(true);

        // Drag source Grid
        Grid<Person> left = createGridAndFillWithData(50);
        GridDragSource<Person> dragSource = applyDragSource(left);

        // Drop target Grid
        Grid<Person> right = createGridAndFillWithData(0);
        GridDropTarget<Person> dropTarget = applyDropTarget(right);

        initializeTestFor(left, right, dragSource, dropTarget);
    }

    private GridDragSource<Person> applyDragSource(Grid<Person> grid) {
        GridDragSource<Person> dragSource = new GridDragSource<>(grid);

        dragSource.setEffectAllowed(EffectAllowed.MOVE);

        // Set data generator
        dragSource.setDragDataGenerator("application/json", person -> {
            StringBuilder builder = new StringBuilder();
            builder.append('{');
            builder.append("\"First Name\":");
            builder.append("\"" + person.getFirstName() + "\"");
            builder.append(',');
            builder.append("\"Last Name\":");
            builder.append("\"" + person.getLastName() + "\"");
            builder.append('}');
            return builder.toString();
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
            log("END: dropEffect=" + event.getDropEffect());
            if (event.getDropEffect() == DropEffect.MOVE
                    && draggedItems != null) {
                // If drop is successful, remove dragged item from source Grid
                ((ListDataProvider<Person>) grid.getDataProvider()).getItems()
                        .removeAll(draggedItems);
                grid.getDataProvider().refreshAll();
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
                    int index = items.size();
                    if (event.getDropTargetRow().isPresent()) {
                        index = items.indexOf(event.getDropTargetRow().get())
                                + (event.getDropLocation() == DropLocation.BELOW
                                        ? 1 : 0);
                    }

                    // Add dragged items to the target Grid
                    items.addAll(index, draggedItems);
                    dataProvider.refreshAll();

                    log("DROP: dragData=" + event.getDataTransferText()
                            + ", dragDataJson="
                            + event.getDataTransferData("application/json")
                            + ", target="
                            + (event.getDropTargetRow().isPresent() ? event
                                    .getDropTargetRow().get().getFirstName()
                                    + " "
                                    + event.getDropTargetRow().get()
                                            .getLastName()
                                    : "[BODY]")
                            + ", location=" + event.getDropLocation()
                            + ", mouseEventDetails="
                            + event.getMouseEventDetails());
                }
            });
        });

        return dropTarget;
    }

}
