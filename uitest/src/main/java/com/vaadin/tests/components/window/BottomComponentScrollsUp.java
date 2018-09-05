package com.vaadin.tests.components.window;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Reproducing bug #12943 where an action on a Button or ComboBox placed at the
 * bottom of a window in a scroll panel, will scroll up the parent panel.
 *
 * This was due to the fact that with the state confirmation notification from
 * the server, the window.setVisible would be call again, and the hack that
 * solved the scrollbars in a window (#11994) would cause the our bug.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class BottomComponentScrollsUp extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button b = new Button("Open window");
        addComponent(b);
        b.addClickListener(event -> openWindow());

        openWindow();
    }

    private void openWindow() {
        Window w = new Window();
        w.setWidth("300px");
        w.setHeight("300px");
        w.center();

        Panel p = createPanel();
        p.setSizeFull();

        w.setContent(p);

        addWindow(w);
    }

    private Panel createPanel() {
        Panel p = new Panel();

        VerticalLayout content = new VerticalLayout();
        content.setMargin(false);
        content.setSpacing(false);
        p.setContent(content);
        content.setHeight("500px");

        List<String> items = new ArrayList<>();
        items.add("1");
        items.add("2");
        items.add("3");

        Button button = new Button("Press me");
        content.addComponent(button);
        content.setComponentAlignment(button, Alignment.BOTTOM_CENTER);
        return p;
    }

    @Override
    protected String getTestDescription() {
        return "Interacting with a component at the bottom of scrollable panel within a subwindow scrolls up";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12943;
    }

}
