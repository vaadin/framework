package com.vaadin.tests.application;

import java.lang.reflect.Type;

import com.vaadin.server.JsonCodec;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.communication.JSONSerializer;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.LoginForm;

import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

public class CustomJSONSerializer extends AbstractTestUI {

    static {
        JsonCodec.setCustomSerializer(URLReference.class,
                new JSONSerializer<URLReference>() {

                    @Override
                    public URLReference deserialize(Type type,
                            JsonValue jsonValue,
                            ConnectorTracker connectorTracker) {
                        // NOP
                        return null;
                    }

                    @Override
                    public JsonValue serialize(URLReference value,
                            ConnectorTracker connectorTracker) {
                        JsonObject result = Json.createObject();
                        String url = value.getURL();
                        // change all test.com urls to vaadin.com
                        if ("http://www.test.com".equals(url)) {
                            url = "http://www.vaadin.com";
                        }
                        result.put("uRL", url);
                        return result;
                    }

                });
    }

    public static class MyLoginForm extends LoginForm {
        public void setResource(URLReference ref) {
            getState().loginResource = ref;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        MyLoginForm loginForm = new MyLoginForm();
        URLReference url = new URLReference();
        url.setURL("http://www.test.com");
        loginForm.setResource(url);
        addComponent(loginForm);
    }

}
