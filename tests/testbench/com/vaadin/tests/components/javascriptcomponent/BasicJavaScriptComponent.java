/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.components.javascriptcomponent;

import java.util.Arrays;
import java.util.List;

import com.vaadin.annotations.LoadScripts;
import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.ui.JavaScriptComponentState;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptCallback;
import com.vaadin.ui.Root;

@LoadScripts({ "/statictestfiles/jsconnector.js" })
public class BasicJavaScriptComponent extends AbstractTestRoot {

    public interface ExampleClickRpc extends ServerRpc {
        public void onClick(String message);
    }

    public static class SpecialState extends JavaScriptComponentState {
        private List<String> data;

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }
    }

    public static class ExampleWidget extends AbstractJavaScriptComponent {
        public ExampleWidget() {
            registerRpc(new ExampleClickRpc() {
                public void onClick(String message) {
                    Root.getCurrentRoot().showNotification(
                            "Got a click: " + message);
                }
            });
            registerCallback("onclick", new JavaScriptCallback() {
                public void call(JSONArray arguments) throws JSONException {
                    Root.getCurrentRoot().showNotification(
                            "Got a callback: " + arguments.getString(0));
                }
            });
            getState().setData(Arrays.asList("a", "b", "c"));
        }

        @Override
        public SpecialState getState() {
            return (SpecialState) super.getState();
        }
    }

    @Override
    protected void setup(WrappedRequest request) {
        ExampleWidget c = new ExampleWidget();
        c.setCaption("test caption");
        c.setDescription("Some description");
        addComponent(c);
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
