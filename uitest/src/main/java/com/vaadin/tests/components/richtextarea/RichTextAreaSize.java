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
package com.vaadin.tests.components.richtextarea;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

public class RichTextAreaSize extends TestBase {

    @Override
    protected String getDescription() {
        return "Test the size of a rich text area. The first area is 100px*100px wide, the second 100%*100% (of 200x200px), the third one has undefined width and height.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2573;
    }

    @Override
    protected void setup() {
        HorizontalLayout main = new HorizontalLayout();
        getMainWindow().setContent(main);

        RichTextArea first = new RichTextArea();
        RichTextArea second = new RichTextArea();
        RichTextArea third = new RichTextArea();

        first.setWidth("150px");
        first.setHeight("400px");
        second.setSizeFull();
        third.setSizeUndefined();

        VerticalLayout secondLayout = new VerticalLayout();
        secondLayout.setWidth("200px");
        secondLayout.setHeight("200px");
        secondLayout.addComponent(second);

        main.addComponent(first);
        main.addComponent(secondLayout);
        main.addComponent(third);
    }

}
