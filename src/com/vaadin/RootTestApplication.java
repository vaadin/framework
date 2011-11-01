package com.vaadin;

import com.vaadin.ui.DefaultRoot;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;

public class RootTestApplication extends Application {
    private final Root root = new DefaultRoot(this, new Label(
            "Roots, bloody roots"));

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public Root getRoot() {
        return root;
    }

}
