package com.vaadin.tests.application;

import java.lang.reflect.Type;

import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.communication.JSONSerializer;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.button.ButtonState;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.LoginForm;

import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

public class CustomJSONSerializer extends AbstractTestUIWithLog {

    public void addSerializer() {
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
                        result.put("uRL", "http://www.vaadin.com");
                        return result;
                    }

                });
    }

    public void removeSerializer() {
        JsonCodec.setCustomSerializer(URLReference.class, null);
    }

    public static class MyLoginForm extends LoginForm {
        public void setResource(URLReference ref) {
            getState().loginResource = ref;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        addSerializer();
        MyLoginForm loginForm = new MyLoginForm();
        URLReference url = new URLReference();
        url.setURL("http://www.google.com");
        loginForm.setResource(url);
        addComponent(loginForm);
        Button remove = new Button("Remove serializer", event -> {
            removeSerializer();
            // update resource to change state
            URLReference url2 = new URLReference();
            url2.setURL("http://www.google.com");
            loginForm.setResource(url2);
        });
        addComponent(remove);
    }

}
