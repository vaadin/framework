package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.LayoutDetector;

@Connect(LayoutDetector.class)
public class LayoutDetectorConnector extends AbstractComponentConnector
        implements PostLayoutListener {
    private int layoutCount = 0;
    private int rpcCount = 0;

    @Override
    protected void init() {
        super.init();
        updateText();

        registerRpc(NoLayoutRpc.class, () -> {
            rpcCount++;
            updateText();
        });
    }

    @Override
    public HTML getWidget() {
        return (HTML) super.getWidget();
    }

    @Override
    public void postLayout() {
        layoutCount++;
        updateText();
    }

    private void updateText() {
        getWidget().setHTML("Layout count: <span id='layoutCount'>"
                + layoutCount + "</span><br />RPC count: <span id='rpcCount'>"
                + rpcCount + "</span>");
    }
}
