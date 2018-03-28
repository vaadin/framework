package com.vaadin.tests.components.javascriptcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Button;

public class StateChangeCounter extends AbstractTestUI {

    @Override
    public String getTestDescription() {
        return "onStateChange should be called only if the state has actually changed";
    }

    @Override
    protected void setup(VaadinRequest request) {
        StateChangeCounterComponent counter = new StateChangeCounterComponent();

        addComponents(new Button("Send RPC", event -> counter.sendRpc()),
                new Button("Change state", event -> counter.changeState()),
                new Button("Mark as dirty", event -> counter.markAsDirty()),
                counter);
    }

    @JavaScript("StateChangeCounter.js")
    public static class StateChangeCounterComponent
            extends AbstractJavaScriptComponent {
        public void sendRpc() {
            callFunction("sendRpc");
        }

        public void changeState() {
            getState().stateCounter++;
        }

        @Override
        protected StateChangeCounterState getState() {
            return (StateChangeCounterState) super.getState();
        }
    }

    public static class StateChangeCounterState
            extends JavaScriptComponentState {
        public int stateCounter = 0;
    }

}
