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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.ColumnResizeEvent;
import com.vaadin.ui.Grid.ColumnResizeListener;

@SuppressWarnings("serial")
public class GridResizeHiddenColumn extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Grid grid = new Grid();
        grid.setContainerDataSource(ComplexPerson.createContainer(100));
        grid.setColumns("firstName", "lastName", "gender", "birthDate");
        grid.getColumn("firstName").setHidable(true);
        grid.getColumn("lastName").setHidable(true).setHidden(true);
        grid.getColumn("gender").setHidable(true).setHidden(true);
        grid.getColumn("birthDate").setHidable(true);

        addComponent(grid);

        grid.addColumnResizeListener(new ColumnResizeListener() {
            @Override
            public void columnResize(ColumnResizeEvent event) {
                log(String.format("Column resized: id=%s, width=%s",
                        event.getColumn().getPropertyId(),
                        event.getColumn().getWidth()));
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Resize columns and then make hidden column visible. The originally hidden column should have an extended width.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19826;
    }

    @Override
    public String getDescription() {
        return "Tests resize when columns with undefined width (-1) are hidden";
    }
}
