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
package com.vaadin.tests.extensions;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

public class UnknownExtensionHandling extends AbstractTestUI {

    // Extension without @Connect counterpart
    public static class MyExtension extends AbstractExtension {
        @Override
        public void extend(AbstractClientConnector target) {
            super.extend(target);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label(
                "A label with a missing extension, should cause sensible output in the debug window / browser console");

        MyExtension extension = new MyExtension();
        extension.extend(label);

        addComponent(label);
    }

}