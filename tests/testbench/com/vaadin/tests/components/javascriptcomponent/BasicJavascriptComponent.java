/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.components.javascriptcomponent;

import java.util.Arrays;
import java.util.List;

import com.vaadin.annotations.LoadScripts;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.AbstractJavascriptComponent;
import com.vaadin.ui.Root;

@LoadScripts({ "/statictestfiles/jsconnector.js" })
public class BasicJavascriptComponent extends AbstractTestRoot {

    public interface ExampleClickRpc extends ServerRpc {
        public void onClick(String message);
    }

    public static class SpecialState extends ComponentState {
        private List<String> data;

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }
    }

    public static class ExampleWidget extends AbstractJavascriptComponent {
        public ExampleWidget() {
            registerRpc(new ExampleClickRpc() {
                public void onClick(String message) {
                    Root.getCurrentRoot().showNotification(
                            "Got a click: " + message);
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
