/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ResynchronizeUI extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Button b = new Button("Resynchronize", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Theme change is currently the only operation which always
                // does resynchronize
                setTheme("runo");
                log("Set theme to runo");
            }
        });
        addComponent(b);
    }
}
