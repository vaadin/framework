/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;

public class GridAlignment extends GridBaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        buildLayout();
        super.setup(request);
        layout.setSizeFull();
    }

    /**
     * Build Layout for test
     */
    private void buildLayout() {
        layout.setColumns(3);
        layout.setRows(3);
        for (int i = 0; i < components.length; i++) {
            layout.addComponent(components[i]);
            layout.setComponentAlignment(components[i], alignments[i]);
        }
    }
}
