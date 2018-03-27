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
package com.vaadin.tests.minitutorials.v7_4;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;

public class UsingGridWithAContainer extends UI {
    @Override
    protected void init(VaadinRequest request) {
        Grid grid = new Grid();
        grid.setContainerDataSource(GridExampleHelper.createContainer());

        grid.getColumn("name").setHeaderCaption("Bean name");
        grid.removeColumn("count");
        grid.setColumnOrder("name", "amount");

        setContent(grid);
    }
}
