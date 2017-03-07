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
package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Window;

public class HugeWindowShouldBeClosable extends TestBase {

    @Override
    protected void setup() {
        Window w = new Window("Hueg");
        w.setWidth("2000px");
        w.setHeight("2000px");
        w.setPositionY(500);
        getMainWindow().addWindow(w);
    }

    @Override
    protected String getDescription() {
        return "Huge windows should be closable";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6784;
    }

}
