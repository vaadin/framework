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
import com.vaadin.v7.ui.ProgressIndicator;

public class SubWindowPositionUpdate extends TestBase {

    static int delay = 400;

    @Override
    protected void setup() {
        Window subWindow = new Window("Draggable sub window") {
            @Override
            public void setPositionX(int positionX) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.setPositionX(positionX);
            }
        };
        getMainWindow().addWindow(subWindow);
        ProgressIndicator pi = new ProgressIndicator();
        pi.setIndeterminate(true);
        pi.setPollingInterval(delay);
        addComponent(pi);
    }

    @Override
    protected String getDescription() {
        return "The window position should not jump inconsistently while "
                + "dragging, even though external UIDL updates are sent and "
                + "received by the progress indicator. A small delay is used "
                + "on the server side to surface the issue (" + delay + "ms).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4427;
    }

}
