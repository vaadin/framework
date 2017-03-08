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
package com.vaadin.tests.elements.progressbar;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ProgressBar;

public class ProgressBarUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ProgressBar complete = new ProgressBar();
        complete.setId("complete");
        complete.setValue(1f);

        ProgressBar halfComplete = new ProgressBar();
        halfComplete.setId("halfComplete");
        halfComplete.setValue(0.5f);

        ProgressBar notStarted = new ProgressBar();
        notStarted.setId("notStarted");
        notStarted.setValue(0f);

        addComponents(complete, halfComplete, notStarted);
    }

    @Override
    protected String getTestDescription() {
        return "Test UI for ProgressBar element API";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }
}
