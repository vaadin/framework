/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.tests.components.abstractfield;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ShortcutWhenBodyFocused extends AbstractTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        Button b = new Button("Hello", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("Hello clicked");
            }
        });
        b.setClickShortcut(KeyCode.A);
        addComponent(b);

        getPage().getStyles().add("body { width: 50% !important}");
    }
}
