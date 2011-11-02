package com.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DefaultRoot;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.Window;

public class RootTestApplication extends Application {
    private final Root root = new DefaultRoot(new Button("Roots, bloody roots",
            new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    Window subWindow = new Window("Sub window");
                    subWindow.addComponent(new Label("More roots"));
                    root.addWindow(subWindow);
                }
            }));

    @Override
    public void init() {
        // TODO Should be done by Application during init
        root.setApplication(this);
    }

    @Override
    public Root getRoot() {
        return root;
    }

}
