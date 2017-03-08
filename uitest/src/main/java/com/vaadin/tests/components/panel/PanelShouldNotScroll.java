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

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PanelShouldNotScroll extends TestBase {

    private Button addMore;

    @Override
    protected void setup() {
        final CssLayout pl = new CssLayout();
        final Panel p = new Panel(pl);
        p.setSizeFull();
        p.setHeight("600px");
        pl.addComponent(foo());
        addMore = new Button("Add");
        addMore.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                pl.removeComponent(addMore);
                pl.addComponent(foo());
                pl.addComponent(addMore);
            }
        });
        pl.addComponent(addMore);
        addComponent(p);
        ((VerticalLayout) getMainWindow().getContent()).setSizeFull();
    }

    private Component foo() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Panel panel = new Panel(layout);
        layout.addComponent(new Label(
                "fooooooooo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>"
                        + "foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>foo<br/>",
                ContentMode.HTML));
        return panel;
    }

    @Override
    protected String getDescription() {
        return "adding a panel to the bottom of the scrolling panel should not scroll up to the top";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7462;
    }

}
