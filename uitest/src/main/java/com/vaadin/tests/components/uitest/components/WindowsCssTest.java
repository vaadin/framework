package com.vaadin.tests.components.uitest.components;

import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ChameleonTheme;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

public class WindowsCssTest extends VerticalLayout {

    private TestSampler parent;
    private String styleName = null;
    private String caption = "A caption";

    private int debugIdCounter = 0;

    public WindowsCssTest(TestSampler parent) {
        this.parent = parent;
        parent.registerComponent(this);

        Button defWindow = new Button("Default window",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        createWindowWith(caption, null, styleName);
                    }
                });
        defWindow.setId("windButton" + debugIdCounter++);
        Button light = new Button("Light window", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                createWindowWith(caption, Reindeer.WINDOW_LIGHT, styleName);
            }
        });
        light.setId("windButton" + debugIdCounter++);
        Button black = new Button("Black window", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                createWindowWith(caption, Reindeer.WINDOW_BLACK, styleName);
            }
        });
        black.setId("windButton" + debugIdCounter++);
        Button dialog = new Button("Dialog window", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                createWindowWith(caption, Runo.WINDOW_DIALOG, styleName);
            }
        });
        dialog.setId("windButton" + debugIdCounter++);
        Button opaque = new Button("Opaque window", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                createWindowWith(caption, ChameleonTheme.WINDOW_OPAQUE,
                        styleName);
            }
        });
        opaque.setId("windButton" + debugIdCounter++);

        addComponent(defWindow);
        addComponent(light);
        addComponent(black);
        addComponent(dialog);
        addComponent(opaque);

    }

    /**
     * 
     * @param caption
     * @param primaryStyleName
     *            - the style defined styleName
     * @param styleName
     *            - the user defined styleName
     * @return
     */
    private void createWindowWith(String caption, String primaryStyleName,
            String styleName) {

        Window window = new Window();
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        window.setContent(layout);
        layout.addComponent(new Label("Some content"));

        if (caption != null) {
            window.setCaption(caption);
        }

        if (primaryStyleName != null) {
            window.addStyleName(primaryStyleName);
        }

        if (styleName != null) {
            window.addStyleName(styleName);
        }

        parent.getUI().addWindow(window);

    }

    @Override
    public void addStyleName(String style) {
        styleName = style;
    }

    @Override
    public void removeStyleName(String style) {
        styleName = null;
    }
}
