package com.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DefaultRoot;
import com.vaadin.ui.Root;

public class RootTestApplication extends Application {
    private final Root root = new DefaultRoot(new Button("Roots, bloody roots",
            new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    root.executeJavaScript("window.alert(\"Here\");");
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
