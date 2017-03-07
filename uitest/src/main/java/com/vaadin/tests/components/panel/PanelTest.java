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
package com.vaadin.tests.components.panel;

import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.Panel;

public class PanelTest<T extends Panel> extends AbstractComponentTest<T> {

    @SuppressWarnings("unchecked")
    @Override
    protected Class<T> getTestClass() {
        return (Class<T>) Panel.class;
    }

}
