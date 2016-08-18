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
import com.vaadin.ui.Grid;

public class NullHeaders extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        grid.addColumn("country", String.class);
        grid.addColumn("foo", String.class);
        grid.addColumn("bar", Integer.class);

        grid.getColumn("country").setHeaderCaption(null);
        grid.getColumn("foo").setHeaderCaption("");
        grid.getColumn("bar").setHeaderCaption(null);
        grid.addRow("Finland", "foo", 1);
        grid.addRow("Swaziland", "bar", 2);
        grid.addRow("Japan", "baz", 3);
        addComponent(grid);
    }

}
