package com.vaadin.tests.components.javascriptcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Label;

import elemental.json.JsonArray;

public class JSComponentLoadingIndicator extends AbstractReindeerTestUI {

    @JavaScript({ "JSComponent.js" })
    public class JSComponent extends AbstractJavaScriptComponent {
        public JSComponent() {
            addFunction("test", new JavaScriptFunction() {
                @Override
                public void call(JsonArray arguments) {
                    try {
                        Thread.sleep(1000);
                        Label label = new Label("pong");
                        label.addStyleName("pong");
                        addComponent(label);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new JSComponent());
    }

}