package com.vaadin.tests.components.splitpanel;

import com.vaadin.terminal.Sizeable;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SplitPanelWidthOnResize extends AbstractTestCase {

    @Override
    public void init() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        Window w = new Window("", layout);
        setMainWindow(w);
        SplitPanel splitPanel = new SplitPanel(
                SplitPanel.ORIENTATION_HORIZONTAL);
        Button button = new NativeButton("A huge button");
        button.setSizeFull();
        TextField textField = new TextField("A small textfield");

        splitPanel.setFirstComponent(button);
        splitPanel.setSecondComponent(textField);
        splitPanel.setSizeFull();
        splitPanel.setSplitPosition(100, Sizeable.UNITS_PERCENTAGE);

        layout.addComponent(splitPanel);
    }

    @Override
    protected String getDescription() {
        return "Make the browser window smaller and then larger again. The huge button should always stay visible and the TextField should never be shown.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3322;
    }

}
