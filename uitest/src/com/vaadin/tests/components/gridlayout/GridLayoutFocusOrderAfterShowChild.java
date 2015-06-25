/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class GridLayoutFocusOrderAfterShowChild extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout gl = new GridLayout(2, 5);
        gl.setId("grid");
        gl.setMargin(true);
        gl.setSpacing(true);

        final Label l1 = new Label("First");
        l1.setWidthUndefined();
        l1.setVisible(false);
        gl.addComponent(l1);

        final TextField t1 = new TextField();
        t1.setId("t1");
        t1.setVisible(false);
        t1.setWidthUndefined();
        gl.addComponent(t1);

        Label l2 = new Label("Second");
        l2.setWidthUndefined();
        gl.addComponent(l2);

        TextField t2 = new TextField();
        t2.setId("t2");
        gl.addComponent(t2);

        final Label l3 = new Label("Third");
        l3.setWidthUndefined();
        l3.setVisible(false);
        gl.addComponent(l3);

        final TextField t3 = new TextField();
        t3.setId("t3");
        t3.setVisible(false);
        gl.addComponent(t3);

        Label l4 = new Label("Fourth");
        l4.setWidthUndefined();
        gl.addComponent(l4);

        TextField t4 = new TextField();
        t4.setId("t4");
        gl.addComponent(t4);

        final Label l5 = new Label("Fifth");
        l5.setWidthUndefined();
        l5.setVisible(false);
        gl.addComponent(l5);

        final TextField t5 = new TextField();
        t5.setId("t5");
        t5.setVisible(false);
        gl.addComponent(t5);

        addComponent(gl);

        addComponent(new Button("Show first", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                t1.setVisible(true);
                l1.setVisible(true);
            }
        }));

        addComponent(new Button("Show third", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                t3.setVisible(true);
                l3.setVisible(true);
            }
        }));

        addComponent(new Button("Show fifth", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                t5.setVisible(true);
                l5.setVisible(true);
            }
        }));
    }
}