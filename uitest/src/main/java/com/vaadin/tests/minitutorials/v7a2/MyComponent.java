package com.vaadin.tests.minitutorials.v7a2;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.tests.widgetset.client.minitutorials.v7a2.MyComponentClientRpc;
import com.vaadin.tests.widgetset.client.minitutorials.v7a2.MyComponentServerRpc;
import com.vaadin.tests.widgetset.client.minitutorials.v7a2.MyComponentState;
import com.vaadin.ui.AbstractComponent;

public class MyComponent extends AbstractComponent {
    private int clickCount = 0;

    private MyComponentServerRpc rpc = new MyComponentServerRpc() {
        @Override
        public void clicked(MouseEventDetails mouseDetails) {
            clickCount++;

            // nag every 5:th click
            if (clickCount % 5 == 0) {
                getRpcProxy(MyComponentClientRpc.class)
                        .alert("Ok, that's enough!");
            }

            setText("You have clicked " + clickCount + " times");
        }
    };

    public MyComponent() {
        registerRpc(rpc);
    }

    @Override
    public MyComponentState getState() {
        return (MyComponentState) super.getState();
    }

    public void setText(String text) {
        getState().text = text;
    }

    public String getText() {
        return getState().text;
    }
}
