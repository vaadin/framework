package com.vaadin.tests.server.component.composite;

import com.vaadin.ui.Composite;
import com.vaadin.ui.NativeButton;

public class MyPrefilledComposite extends Composite {
    public MyPrefilledComposite() {
        setCompositionRoot(new NativeButton());
    }
}
