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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;

public class GridCheckBoxDisplay extends AbstractReindeerTestUI {

    private static final long serialVersionUID = -5575892909354637168L;

    @Override
    protected void setup(VaadinRequest request) {
        List<Todo> items = Arrays.asList(new Todo("Done task", true),
                new Todo("Not done", false));

        Grid<Todo> grid = new Grid<>();
        grid.setSizeFull();

        TextField taskField = new TextField();
        CheckBox doneField = new CheckBox();

        Binder<Todo> binder = grid.getEditor().getBinder();

        Binding<Todo, Boolean> doneBinding = binder.bind(doneField,
                Todo::isDone, Todo::setDone);

        Column<Todo, String> column = grid
                .addColumn(todo -> String.valueOf(todo.isDone()));
        column.setWidth(75);
        column.setEditorBinding(doneBinding);

        grid.addColumn(Todo::getTask).setExpandRatio(1)
                .setEditorComponent(taskField, Todo::setTask);

        grid.getEditor().setEnabled(true);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.setItems(items);

        getLayout().addComponent(grid);
        getLayout().setExpandRatio(grid, 1);

    }

    @Override
    protected Integer getTicketNumber() {
        return 16976;
    }

    @Override
    protected String getTestDescription() {
        return "Verify that checkbox state is correct for all items in editor";
    }

    public class Todo implements Serializable {
        private static final long serialVersionUID = -5961103142478316018L;

        private boolean done;
        private String task = "";

        public Todo(String task, boolean done) {
            this.task = task;
            this.done = done;
        }

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }
    }

}
