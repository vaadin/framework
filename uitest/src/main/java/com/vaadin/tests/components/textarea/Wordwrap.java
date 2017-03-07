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
package com.vaadin.tests.components.textarea;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;

public class Wordwrap extends TestBase {

    @Override
    public void setup() {
        HorizontalLayout layout = new HorizontalLayout();

        TextArea area1 = new TextArea("Wrapping");
        area1.setWordWrap(true); // The default
        area1.setValue(LoremIpsum.get(50) + "\n" + "Another row");

        final TextArea area2 = new TextArea("Nonwrapping");
        area2.setWordWrap(false);
        area2.setValue(LoremIpsum.get(50) + "\n" + "Another row");

        layout.addComponent(area1);
        layout.addComponent(area2);
        layout.setSpacing(true);

        addComponent(layout);

        CheckBox onoff = new CheckBox("Wrap state for the right field");
        onoff.setValue(false);
        onoff.addValueChangeListener(event -> {
            boolean wrap = event.getValue();
            area2.setWordWrap(wrap);
            if (wrap) {
                area2.setCaption("Wrapping");
            } else {
                area2.setCaption("Nonwrapping");
            }
        });

        addComponent(onoff);
    }

    @Override
    protected String getDescription() {
        return "";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6003;
    }
}
