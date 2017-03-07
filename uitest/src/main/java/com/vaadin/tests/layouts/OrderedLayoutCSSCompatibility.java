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
package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.TextField;

public class OrderedLayoutCSSCompatibility extends TestBase {

    @Override
    protected String getDescription() {
        return "This test is to make sure that spacing/margins in OrderedLayout is still backwards compatible";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2463;
    }

    @Override
    protected void setup() {
        HorizontalLayout l = new HorizontalLayout();
        l.setMargin(true);
        l.setSpacing(true);
        l.addComponent(new TextField("abc"));
        l.addComponent(new TextField("def"));

        addComponent(l);

    }

}
