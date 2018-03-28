package com.vaadin.tests.components.javascriptcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Button;

public class JavaScriptNoLayoutHandlingUI extends AbstractTestUIWithLog {

    public static class MyJSComponentState extends JavaScriptComponentState {
        // Using public methods as these are handled before public fields in the
        // parent
        private int aaa = 1;

        public int getAaa() {
            return aaa;
        }

        public void setAaa(int aaa) {
            this.aaa = aaa;
        }
    }

    @JavaScript("MyJS.js")
    public static class MyJsComponent extends AbstractJavaScriptComponent {

        @Override
        protected MyJSComponentState getState() {
            return (MyJSComponentState) super.getState();
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        final MyJsComponent myComponent = new MyJsComponent();
        myComponent.setId("js");
        addComponent(myComponent);
        addComponent(new Button("Send update",
                event -> myComponent.getState().aaa++));
    }

}
