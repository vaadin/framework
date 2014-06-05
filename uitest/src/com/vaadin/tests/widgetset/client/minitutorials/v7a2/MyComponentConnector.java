package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.minitutorials.v7a2.MyComponent;

@Connect(MyComponent.class)
public class MyComponentConnector extends AbstractComponentConnector {

    MyComponentServerRpc rpc = RpcProxy
            .create(MyComponentServerRpc.class, this);

    public MyComponentConnector() {
        getWidget().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final MouseEventDetails mouseDetails = MouseEventDetailsBuilder
                        .buildMouseEventDetails(event.getNativeEvent(),
                                getWidget().getElement());

                rpc.clicked(mouseDetails);
            }
        });
        registerRpc(MyComponentClientRpc.class, new MyComponentClientRpc() {
            @Override
            public void alert(String message) {
                Window.alert(message);
            }
        });
    }

    @Override
    public MyComponentState getState() {
        return (MyComponentState) super.getState();
    }

    @OnStateChange("text")
    void updateText() {
        getWidget().setText(getState().text);
    }

    @Override
    public MyComponentWidget getWidget() {
        return (MyComponentWidget) super.getWidget();
    }

}
