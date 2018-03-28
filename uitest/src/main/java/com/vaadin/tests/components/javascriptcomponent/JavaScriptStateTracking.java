package com.vaadin.tests.components.javascriptcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Constants;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Button;

@JavaScript("JavaScriptStateTracking.js")
@Widgetset(Constants.DEFAULT_WIDGETSET)
public class JavaScriptStateTracking extends AbstractTestUI {

    public static class StateTrackingComponentState
            extends JavaScriptComponentState {
        public int counter = 0;
        public String field1 = "initial value";
        public String field2;
    }

    public static class StateTrackingComponent
            extends AbstractJavaScriptComponent {
        @Override
        protected StateTrackingComponentState getState() {
            return (StateTrackingComponentState) super.getState();
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        StateTrackingComponent stateTrackingComponent = new StateTrackingComponent();

        Button setField2 = new Button("Set field2", event -> {
            stateTrackingComponent.getState().counter++;
            stateTrackingComponent.getState().field2 = "updated value "
                    + stateTrackingComponent.getState().counter;
        });
        setField2.setId("setField2");

        Button clearField1 = new Button("Clear field1", event -> {
            stateTrackingComponent.getState().counter++;
            stateTrackingComponent.getState().field1 = null;
        });
        clearField1.setId("clearField1");

        addComponents(stateTrackingComponent, setField2, clearField1);
    }

}
