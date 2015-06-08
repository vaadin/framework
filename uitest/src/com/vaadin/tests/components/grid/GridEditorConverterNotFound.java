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

import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Grid;

public class GridEditorConverterNotFound extends AbstractTestUIWithLog {

    class Foo {
    }

    @Override
    protected void setup(VaadinRequest request) {

        Grid grid = new Grid();

        grid.addColumn("foo", Foo.class);
        grid.addRow(new Foo());
        grid.setEditorEnabled(true);
        grid.setErrorHandler(new ErrorHandler() {

            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                log(event.getThrowable().toString());
            }
        });

        addComponent(grid);
    }

    @Override
    protected Integer getTicketNumber() {
        return 17935;
    }

    @Override
    protected String getTestDescription() {
        return "Grid should gracefully handle bind failures when opening editor";
    }
}
