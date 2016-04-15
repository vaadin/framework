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
package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class WindowCaption extends AbstractTestUI {

    private Window htmlWindow;
    private Window textWindow;

    @Override
    protected void setup(VaadinRequest request) {
        htmlWindow = new Window("", new Label("HTML caption"));
        htmlWindow.setId("htmlWindow");
        htmlWindow.setCaptionAsHtml(true);
        htmlWindow.setPositionX(300);
        htmlWindow.setPositionY(200);

        textWindow = new Window("", new Label("Text caption"));
        textWindow.setId("textWindow");
        textWindow.setCaptionAsHtml(false);
        textWindow.setPositionX(300);
        textWindow.setPositionY(400);

        addWindow(htmlWindow);
        addWindow(textWindow);

        Button red = new Button("Red", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setWindowCaption("<font style='color: red;'>This may or may not be red</font>");
            }
        });
        Button plainText = new Button("Plain text", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setWindowCaption("This is just text");
            }
        });
        Button nullCaption = new Button("Null", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setWindowCaption(null);
            }
        });
        Button empty = new Button("Empty", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setWindowCaption("");
            }
        });

        addComponents(red, plainText, nullCaption, empty);
        red.click();
    }

    private void setWindowCaption(String string) {
        htmlWindow.setCaption(string);
        textWindow.setCaption(string);
    }

}
