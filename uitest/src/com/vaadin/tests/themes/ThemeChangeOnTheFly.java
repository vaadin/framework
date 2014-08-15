/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.themes;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Theme("reindeer")
public class ThemeChangeOnTheFly extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Button inject = new Button("Inject blue background");
        inject.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getPage().getStyles().add(
                        ".v-app { background: blue !important;}");

            }
        });
        addComponent(inject);

        GridLayout gl = new GridLayout(2, 4);
        gl.setCaption("Change theme by clicking a button");
        for (final String theme : new String[] { "reindeer", "runo",
                "chameleon", "base", null }) {
            Button b = new Button(theme);
            b.setId(theme + "");
            b.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    getUI().setTheme(theme);
                }
            });
            gl.addComponent(b);
        }

        Table t = new Table();
        PersonContainer pc = PersonContainer.createWithTestData();
        pc.addNestedContainerBean("address");
        t.setContainerDataSource(pc);
        gl.addComponent(t, 0, 3, 1, 3);
        gl.setRowExpandRatio(3, 1);

        gl.setWidth("500px");
        gl.setHeight("800px");

        HorizontalLayout images = new HorizontalLayout();
        images.setSpacing(true);

        Label l = new Label("Chameleon theme image in caption");
        l.setIcon(new ThemeResource("img/magnifier.png"));
        images.addComponent(l);
        Image image = new Image("Runo theme image", new ThemeResource(
                "icons/64/ok.png"));
        images.addComponent(image);
        image = new Image("Reindeer theme image", new ThemeResource(
                "button/img/left-focus.png"));
        images.addComponent(image);
        addComponent(images);
        addComponent(gl);

        getLayout().setSpacing(true);

        Window w = new Window();
        w.setContent(new VerticalLayout(new Button("Button in window")));
        addWindow(w);
    }

    @Override
    protected String getTestDescription() {
        return "Test that you can change theme on the fly";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2874;
    }

}
