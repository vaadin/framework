package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@Theme(ValoTheme.THEME_NAME)
public class WindowTitleOverflow extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addButton("Open Resizable", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                addWindow(getWindow(true, false));
            }
        });

        addButton("Open Closable", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                addWindow(getWindow(false, true));
            }
        });

        addButton("Open Resizable and Closable", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                addWindow(getWindow(true, true));
            }
        });

        addButton("Open Non-Resizable and Non-Closable",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        addWindow(getWindow(false, false));
                    }
                });
    }

    private Window getWindow(boolean resizable, boolean closable) {
        Window window = new Window();

        window.setModal(true);
        window.setResizable(resizable);
        window.setClosable(closable);
        window.setCaption("Long Foobar Foobar Foobar Foobar Foobar Foobar");

        return window;
    }

    @Override
    protected Integer getTicketNumber() {
        return 15408;
    }

    @Override
    protected String getTestDescription() {
        return "In Valo, header title should use the space of hidden buttons.";
    }
}
