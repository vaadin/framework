package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.WindowDesktop;

public class LimitedAreaSubWindow extends AbstractTestUIWithLog {
    WindowDesktop swd;
    
    @Override
    protected void setup(VaadinRequest request) {
        Button openWindowButton = new Button("Open sub-window");
        openWindowButton.setId("opensub");
        openWindowButton.addClickListener(event -> {
            Window sub = createClosableSubWindow("Sub-window");
            swd.addSubWindow(sub);
        });

        Panel upperPanel = new Panel();
        HorizontalLayout hlupper = new HorizontalLayout();
        hlupper.addComponent(openWindowButton);
        upperPanel.setContent(hlupper);
        
        swd = new WindowDesktop();
        swd.setSizeFull();
        
        Panel left = new Panel();

        HorizontalSplitPanel hsp = new HorizontalSplitPanel(left,swd);
        hsp.setSplitPosition(60, Unit.PIXELS);
        
        VerticalSplitPanel vsp = new VerticalSplitPanel(upperPanel,hsp);
        vsp.setSplitPosition(200, Unit.PIXELS);
        
        setContent(vsp);
        
    }

    private Window createClosableSubWindow(final String title) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSizeUndefined();
        final Window window = new Window(title, layout);
        window.setSizeUndefined();
        window.setClosable(true);

        Button closeButton = new Button("Close");
        closeButton.addClickListener(
                event -> event.getButton().findAncestor(Window.class).close());
        layout.addComponent(closeButton);

        Button removeButton = new Button("Remove from UI");
        removeButton.addClickListener(event -> swd.removeWindow(window));
        layout.addComponent(removeButton);

        window.addCloseListener(event -> log("Window '" + title + "' closed"));

        return window;
    }

    @Override
    protected String getTestDescription() {
        return "Close sub-windows both from code and with the close button in the window title bar, and check for close events. Contains an ugly workaround for the Opera bug (Opera does not send close events)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3865;
    }
}
