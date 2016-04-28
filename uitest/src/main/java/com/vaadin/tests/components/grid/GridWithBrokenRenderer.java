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

import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.integration.FlagSeResource;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ImageRenderer;

public class GridWithBrokenRenderer extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        grid.addColumn("short", String.class);
        grid.addColumn("icon", Resource.class);
        grid.addColumn("country", String.class);

        grid.getColumn("icon").setRenderer(new ImageRenderer());
        addComponent(grid);

        grid.addRow("FI", new ClassResource("fi.gif"), "Finland");
        grid.addRow("SE", new FlagSeResource(), "Sweden");

    }

}
