package com.itmill.toolkit.tests.components.window;

import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.ResizeEvent;
import com.itmill.toolkit.ui.Window.ResizeListener;

public class WindowResizeListener extends TestBase {

    @Override
    protected String getDescription() {
        return "Size changes from windows (both sub "
                + "and browsers level) should get back to server."
                + " If size changes, a separate server side event should occur.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    Window subwin = new ResizeListenerWindow();

    @Override
    protected void setup() {

        final Label l = new Label();
        getLayout().addComponent(l);

        getMainWindow().addListener(new ResizeListener() {
            public void windowResized(ResizeEvent e) {
                l.setValue("Current main window size: "
                        + getMainWindow().getWidth() + " x "
                        + getMainWindow().getHeight());
            }
        });

        CheckBox subwindow = new CheckBox("show subwindow");
        subwindow.setImmediate(true);
        subwindow.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (event.getButton().booleanValue()) {
                    getMainWindow().addWindow(subwin);
                } else {
                    getMainWindow().removeWindow(subwin);
                }
            }
        });
        getLayout().addComponent(subwindow);

        CheckBox immediate = new CheckBox("immediate");
        immediate.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                boolean booleanValue = event.getButton().booleanValue();
                getMainWindow().setImmediate(booleanValue);
                subwin.setImmediate(booleanValue);
            }
        });
        immediate.setImmediate(true);
        getLayout().addComponent(immediate);

        getLayout().addComponent(new Button("Sync"));

    }
}

class ResizeListenerWindow extends Window {
    Label sizeLabel = new Label();

    public ResizeListenerWindow() {
        super("Subwindow");
        setWidth("400px");

        Layout hl = getLayout();
        hl.addComponent(new Label("Current size: "));
        hl.addComponent(sizeLabel);

        addListener(new ResizeListener() {
            public void windowResized(ResizeEvent e) {
                updateLabel();
            }
        });

        updateLabel();
    }

    public void updateLabel() {
        sizeLabel
                .setValue(getWidth() + Sizeable.UNIT_SYMBOLS[getWidthUnits()]
                        + " x " + getHeight()
                        + Sizeable.UNIT_SYMBOLS[getHeightUnits()]);
    }
}
