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
package com.vaadin.tests.containers;

import java.io.File;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.data.util.FilesystemContainer;
import com.vaadin.v7.ui.Table;

public class TableWithFileSystemContainer extends TestBase {

    private String testPath = "C:/temp/img";

    @Override
    public void setup() {
        Table table = new Table("Documents",
                new FilesystemContainer(new File(testPath)));
        table.setWidth("100%");
        getMainWindow().addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "The Table uses a FileSystemContainer as datasource. Scrolling to the end should show the last items, not throw an NPE.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3864;
    }

}
