package com.vaadin.tests.browserfeatures;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.ListSelect;

@SuppressWarnings("serial")
public class WebkitScrollbarTest extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout uiLayout = new VerticalLayout();
        uiLayout.setMargin(true);
        setContent(uiLayout);

        final VerticalLayout windowLayout = new VerticalLayout();

        final Window testWindow = new Window("WebKitFail", windowLayout);
        testWindow.setWidth(300, Unit.PIXELS);

        GridLayout gl = new GridLayout();
        gl.setHeight(null);
        gl.setWidth(100, Unit.PERCENTAGE);
        windowLayout.addComponent(gl);

        ListSelect listSelect = new ListSelect();
        listSelect.setWidth(100, Unit.PERCENTAGE);
        gl.addComponent(listSelect);
        gl.setMargin(true);

        final Button testButton = new Button("Open Window",
                event -> UI.getCurrent().addWindow(testWindow));
        uiLayout.addComponent(testButton);
    }

    @Override
    protected String getTestDescription() {
        return "When opening the window, it should NOT contain a horizontal"
                + " scrollbar and the vertical height should be proportional"
                + " to the list select component inside it.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11994;
    }

}
