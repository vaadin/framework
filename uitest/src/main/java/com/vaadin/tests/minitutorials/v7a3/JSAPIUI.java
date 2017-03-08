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
package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import elemental.json.JsonArray;
import elemental.json.JsonException;

public class JSAPIUI extends UI {
    @Override
    public void init(VaadinRequest request) {

        JavaScript.getCurrent().addFunction("com.example.api.notify",
                new JavaScriptFunction() {
                    @Override
                    public void call(JsonArray arguments) {
                        try {
                            String caption = arguments.getString(0);
                            if (arguments.length() == 1) {
                                // only caption
                                Notification.show(caption);
                            } else {
                                // type should be in [1]
                                Notification.show(caption,
                                        Type.values()[((int) arguments
                                                .getNumber(1))]);
                            }

                        } catch (JsonException e) {
                            // We'll log in the console, you might not want to
                            JavaScript.getCurrent().execute(
                                    "console.error('" + e.getMessage() + "')");
                        }
                    }
                });

        setContent(new Link("Send message", new ExternalResource(
                "javascript:(function(){com.example.api.notify(prompt('Message'),2);})();")));
    }
}
