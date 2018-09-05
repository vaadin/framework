package com.vaadin.tests.server.component.customcomponent;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.NativeButton;

public class MyPrefilledCustomComponent extends CustomComponent {
    public MyPrefilledCustomComponent() {
        setCompositionRoot(new NativeButton());
    }
}
