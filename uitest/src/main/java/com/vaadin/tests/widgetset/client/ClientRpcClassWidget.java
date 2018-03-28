package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.ui.MediaControl;
import com.vaadin.v7.client.ui.VLabel;

public class ClientRpcClassWidget extends VLabel implements MediaControl {

    @Override
    public void play() {
        setText("play");
    }

    @Override
    public void pause() {
        setText("pause");
    }

}
