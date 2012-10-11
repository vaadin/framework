/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.tests.minitutorials.v7b2;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class CleanupUI extends UI implements UI.CleanupListener {
    @Override
    protected void init(VaadinRequest request) {
        addCleanupListener(new UI.CleanupListener() {
            @Override
            public void cleanup(UI.CleanupEvent event) {
                releaseSomeResources();
            }
        });

        // ...
        addCleanupListener(this);
    }

    private void releaseSomeResources() {
        // ...
    }

    @Override
    public void cleanup(UI.CleanupEvent event) {
        // do cleanup
        event.getUI();
        // or equivalent:
        UI.getCurrent();
    }
}
