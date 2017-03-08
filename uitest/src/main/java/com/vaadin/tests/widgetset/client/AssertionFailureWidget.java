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
package com.vaadin.tests.widgetset.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ui.VLabel;

public class AssertionFailureWidget extends SimplePanel {

    public AssertionFailureWidget() {
        Scheduler.get().scheduleDeferred(() -> {
            assert 1 == 2 : "This should fail.";
            VLabel w = new VLabel();
            add(w);
            w.setText("This should not be here.");
            w.addStyleName("non-existent-widget");
        });
    }
}
