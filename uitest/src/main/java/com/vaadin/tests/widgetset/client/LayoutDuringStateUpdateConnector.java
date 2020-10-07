package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.LayoutDuringStateUpdateComponent;

@Connect(LayoutDuringStateUpdateComponent.class)
public class LayoutDuringStateUpdateConnector extends AbstractComponentConnector
        implements PostLayoutListener {
    private int layoutCount = 0;
    private String changeLog = "";

    @Override
    protected void init() {
        super.init();
        updateLabelText();
    }

    @Override
    public Label getWidget() {
        return (Label) super.getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        try {
            getLayoutManager().layoutNow();

            // these should never be reached
            if (changeLog.isEmpty()) {
                changeLog += " properties: ";
            }
            for (String property : stateChangeEvent.getChangedProperties()) {
                changeLog += property + ", ";
            }
        } catch (AssertionError e) {
            // Ignore
        }
    }

    private void updateLabelText() {
        getWidget().setText("Layout phase count: " + layoutCount + changeLog);
        changeLog = "";
    }

    @Override
    public void postLayout() {
        layoutCount++;
        updateLabelText();
    }

}
