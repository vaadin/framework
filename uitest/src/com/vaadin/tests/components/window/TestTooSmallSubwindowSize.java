package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Tests that the styles work correctly in tiny subwindows that have more
 * content than can fit.
 * 
 * @author Vaadin Ltd
 */
public class TestTooSmallSubwindowSize extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "The size of the subwindows (outer size) is set to 60x60 pixels. Make sure the shadows fits the windows instead of the contents. The decorations at the lower right corners of the resizable windows must not be missing either.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2579;
    }

    @Override
    protected void setup(VaadinRequest request) {
        getUI().addWindow(createNonResizableWindow());
        getUI().addWindow(createNonResizableWindowWithHorizontalScrollbar());
        getUI().addWindow(createResizableWindow());
        getUI().addWindow(createResizableWindowWithHorizontalScrollbar());
    }

    private Window createNonResizableWindow() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Window w = new Window("Scroll", layout);
        Label desc = new Label(
                "This is a new child window with a preset"
                        + " width, height and position. Resizing has been"
                        + " disabled for this window. Additionally, this text label"
                        + " is intentionally too large to fit the window. You can"
                        + " use the scrollbars to view different parts of the window content.");
        layout.addComponent(desc);

        // Set window position
        w.setPositionX(100);
        w.setPositionY(100);

        // Set window size
        w.setWidth(60, Unit.PIXELS);
        w.setHeight(60, Unit.PIXELS);

        // Disable resizing
        w.setResizable(false);

        return w;
    }

    private Window createNonResizableWindowWithHorizontalScrollbar() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Window w = new Window("Scroll", layout);
        Label desc = new Label(
                "This is a new child window with a preset"
                        + " width, height and position. Resizing has been"
                        + " disabled for this window. Additionally, this text label"
                        + " is intentionally too large to fit the window. You could"
                        + " use the scrollbars to view different parts of the window content,"
                        + " except it's too small for that either.");
        // disable wrapping
        desc.setSizeUndefined();
        layout.addComponent(desc);

        // Set window position
        w.setPositionX(200);
        w.setPositionY(100);

        // Set window size
        w.setWidth(60, Unit.PIXELS);
        w.setHeight(60, Unit.PIXELS);

        // Disable resizing
        w.setResizable(false);

        return w;
    }

    private Window createResizableWindow() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Window w = new Window("Resize", layout);
        Label desc = new Label(
                "This is a new child window with a preset"
                        + " width, height and position. Resizing has not been"
                        + " disabled for this window. Additionally, this text label"
                        + " is intentionally too large to fit the window. You can resize or"
                        + " use the scrollbars to view different parts of the window content.");
        layout.addComponent(desc);

        // Set window position
        w.setPositionX(300);
        w.setPositionY(100);

        // Set window size
        w.setWidth(60, Unit.PIXELS);
        w.setHeight(60, Unit.PIXELS);

        // Don't disable resizing

        return w;
    }

    private Window createResizableWindowWithHorizontalScrollbar() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Window w = new Window("Resize", layout);
        Label desc = new Label(
                "This is a new child window with a preset"
                        + " width, height and position. Resizing has not been"
                        + " disabled for this window. Additionally, this text label"
                        + " is intentionally too large to fit the window. You can resize"
                        + " to view different parts of the window content.");
        // disable wrapping
        desc.setSizeUndefined();
        layout.addComponent(desc);

        // Set window position
        w.setPositionX(400);
        w.setPositionY(100);

        // Set window size
        w.setWidth(60, Unit.PIXELS);
        w.setHeight(60, Unit.PIXELS);

        // Don't disable resizing

        return w;
    }

}
