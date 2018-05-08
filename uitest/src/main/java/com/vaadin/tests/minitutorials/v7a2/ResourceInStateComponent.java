package com.vaadin.tests.minitutorials.v7a2;

import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractComponent;

public class ResourceInStateComponent extends AbstractComponent {

    public void setMyIcon(Resource icon) {
        setResource("myIcon", icon);
    }

    public Resource getMyIcon() {
        return getResource("myIcon");
    }
}
