package com.vaadin.tests;

import java.net.MalformedURLException;
import java.net.URL;

import com.vaadin.server.LegacyApplication;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class NativeWindowing extends LegacyApplication {

    LegacyWindow main = new LegacyWindow("Windowing test");

    @Override
    public void init() {

        setMainWindow(main);

        main.addComponent(new Button("Add new subwindow", event -> {
            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            final Window w = new Window("sw " + System.currentTimeMillis(),
                    layout);
            main.addWindow(w);
            w.setPositionX(100);
            w.setPositionY(100);
            w.setWidth("200px");
            w.setHeight("200px");

            w.setWidth("100px");
            w.setHeight("400px");

            final Button closebutton = new Button("Close " + w.getCaption(),
                    clickEvent -> main.removeWindow(w));
            layout.addComponent(closebutton);

            layout.addComponent(new Label(
                    "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>"
                            + "<p>Lorem ipsum dolor sit amet.</p>",
                    ContentMode.HTML));
        }));

        main.addComponent(new Button(
                "Open a currently uncreated application level window",
                event -> {
                    try {
                        main.open(new com.vaadin.server.ExternalResource(
                                new URL(getURL(), "mainwin-"
                                        + System.currentTimeMillis() + "/")),
                                null);
                    } catch (final MalformedURLException e) {
                    }
                }));

        main.addComponent(new Button(
                "Commit (saves window state: size, place, scrollpos)"));
    }

    @Override
    public LegacyWindow getWindow(String name) {

        final LegacyWindow w = super.getWindow(name);
        if (w != null) {
            return w;
        }

        if (name != null && name.startsWith("mainwin-")) {
            final String postfix = name.substring("mainwin-".length());
            final LegacyWindow ww = new LegacyWindow("Window: " + postfix);
            ww.setName(name);
            ww.addComponent(new Label(
                    "This is a application-level window opened with name: "
                            + name));
            ww.addComponent(new Button("Click me", new Button.ClickListener() {
                int state = 0;

                @Override
                public void buttonClick(ClickEvent event) {
                    ww.addComponent(new Label(
                            "Button clicked " + (++state) + " times"));
                }
            }));
            addWindow(ww);
            return ww;
        }

        return null;
    }

}
