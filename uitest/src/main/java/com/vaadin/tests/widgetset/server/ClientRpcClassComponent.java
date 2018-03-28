package com.vaadin.tests.widgetset.server;

import com.vaadin.shared.ui.MediaControl;
import com.vaadin.ui.Label;

public class ClientRpcClassComponent extends Label {
    public void play() {
        getRpcProxy(MediaControl.class).play();
    }

    public void pause() {
        getRpcProxy(MediaControl.class).pause();
    }
}
