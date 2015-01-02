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

        setContent(new Link(
                "Send message",
                new ExternalResource(
                        "javascript:(function(){com.example.api.notify(prompt('Message'),2);})();")));
    }
}
