package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.MouseEventDetailsBuilder;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.tests.minitutorials.v7a2.MyComponent;

@Connect(MyComponent.class)
public class MyComponentConnector extends AbstractComponentConnector {

    MyComponentServerRpc rpc = RpcProxy
            .create(MyComponentServerRpc.class, this);

    public MyComponentConnector() {
        getWidget().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

                final MouseEventDetails mouseDetails = MouseEventDetailsBuilder
                        .buildMouseEventDetails(event.getNativeEvent(),
                                getWidget().getElement());

                rpc.clicked(mouseDetails);
            }
        });
        registerRpc(MyComponentClientRpc.class, new MyComponentClientRpc() {
            public void alert(String message) {
                Window.alert(message);
            }
        });
    }

    @Override
    public MyComponentState getState() {
        return (MyComponentState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        final String text = getState().getText();
        getWidget().setText(text);
    }

    @Override
    public MyComponentWidget getWidget() {
        return (MyComponentWidget) super.getWidget();
    }

}