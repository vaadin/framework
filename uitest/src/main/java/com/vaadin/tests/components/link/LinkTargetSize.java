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
package com.vaadin.tests.components.link;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Link;

public class LinkTargetSize extends TestBase {

    @Override
    protected String getDescription() {
        return "This link should open a small window w/o decorations";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2524;
    }

    @Override
    protected void setup() {
        Link l = new Link("Try it!",
                new ExternalResource("http://www.google.com/m"));
        l.setTargetName("_blank");
        l.setTargetWidth(300);
        l.setTargetHeight(300);
        l.setTargetBorder(BorderStyle.NONE);
        addComponent(l);
    }

}
