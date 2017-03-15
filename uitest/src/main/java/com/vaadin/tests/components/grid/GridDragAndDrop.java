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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.vaadin.event.dnd.grid.GridDropListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.GridDragSourceExtensionState;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridDragSourceExtension;
import com.vaadin.ui.GridDropTargetExtension;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;

import elemental.json.Json;
import elemental.json.JsonObject;

public class GridDragAndDrop extends AbstractTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        // Drag source Grid
        Grid<Bean> dragSourceComponent = new Grid<>();
        dragSourceComponent.setItems(createItems(50));
        dragSourceComponent.addColumn(Bean::getId).setCaption("ID");
        dragSourceComponent.addColumn(Bean::getValue).setCaption("Value");

        GridDragSourceExtension<Bean> dragSource = new GridDragSourceExtension<>(
                dragSourceComponent);
        dragSource.setDragDataGenerator(bean -> {
            JsonObject ret = Json.createObject();
            ret.put("val", bean.getValue());
            return ret;
        });

        // Drop target Grid
        Grid<Bean> dropTargetComponent = new Grid<>();
        dropTargetComponent.setItems(createItems(5));
        dropTargetComponent.addColumn(Bean::getId).setCaption("ID");
        dropTargetComponent.addColumn(Bean::getValue).setCaption("Value");

        GridDropTargetExtension<Bean> dropTarget = new GridDropTargetExtension<>(
                dropTargetComponent);
        dropTarget.addDropListener((GridDropListener<Bean>) event -> {
            log(event.getTransferData(
                    GridDragSourceExtensionState.DATA_TYPE_DRAG_DATA)
                    + ", targetId=" + event.getDropTargetRow().getId());
        });

        // Layout grids
        Layout layout = new HorizontalLayout();
        layout.addComponents(dragSourceComponent, dropTargetComponent);

        // Selection mode combo box
        ComboBox<Grid.SelectionMode> selectionModeSwitch = new ComboBox<>(
                "Change selection mode");
        selectionModeSwitch.setItems(Arrays.asList(Grid.SelectionMode.SINGLE,
                Grid.SelectionMode.MULTI));
        selectionModeSwitch.setEmptySelectionAllowed(false);
        selectionModeSwitch.addValueChangeListener(event -> dragSourceComponent
                .setSelectionMode(event.getValue()));
        selectionModeSwitch.setSelectedItem(Grid.SelectionMode.SINGLE);

        addComponents(selectionModeSwitch, layout);

        // Set dragover styling
        Page.getCurrent().getStyles().add(".v-drag-over {color: red;}");
    }

    private List<Bean> createItems(int num) {
        List<Bean> items = new ArrayList<>(num);

        IntStream.range(0, num)
                .forEach(i -> items.add(new Bean("id_" + i, "value_" + i)));

        return items;
    }

    public static class Bean {
        private String id;
        private String value;

        public Bean(String id, String value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {

            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
