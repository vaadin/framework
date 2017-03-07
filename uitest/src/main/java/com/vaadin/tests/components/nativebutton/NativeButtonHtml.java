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
package com.vaadin.tests.components.nativebutton;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.NativeButton;

public class NativeButtonHtml extends TestBase {

    @Override
    protected void setup() {
        NativeButton b = new NativeButton("<b>Plain text button</b>");
        addComponent(b);

        b = new NativeButton(
                "<span style=\"color: red; font-weight: bold;\">HTML</span> button");
        b.setCaptionAsHtml(true);
        addComponent(b);

        final NativeButton swapButton = new NativeButton("<i>Swap button<i>");
        swapButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                swapButton.setCaptionAsHtml(!swapButton.isCaptionAsHtml());
            }
        });
        addComponent(swapButton);
    }

    @Override
    protected String getDescription() {
        return "Verify that NativeButton HTML rendering works";
    }

    @Override
    protected Integer getTicketNumber() {
        // 8663 was for normal button (see ButtonHtml test)
        return null;
    }
}
