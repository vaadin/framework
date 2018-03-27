/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Push
@Widgetset("com.vaadin.DefaultWidgetSet")
public class PushToggleComponentVisibility extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout mainLayout = new VerticalLayout();
        setContent(mainLayout);

        Label label = new Label("Please wait");
        label.setId("label");
        label.setVisible(false);
        mainLayout.addComponent(label);

        Button button = new Button("Hide me for 3 seconds");
        button.setId("hide");
        button.addClickListener(event1 -> {
            button.setVisible(false);
            label.setVisible(true);

            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                button.getUI().access(() -> {
                    button.setVisible(true);
                    label.setVisible(false);
                    button.getUI().push();
                });
            }).start();
        });
        mainLayout.addComponent(button);
    }

}
