package com.vaadin.tests.dd;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.UI;

public class SpacebarPanner extends AbstractExtension {

    private static final long serialVersionUID = -7712258690917457123L;

    public static SpacebarPanner wrap(UI ui) {
        SpacebarPanner panner = new SpacebarPanner();
        panner.extend(ui);
        return panner;
    }

    public void interruptNext() {
        getState().enabled = !getState().enabled;
    }

}
