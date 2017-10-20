package com.vaadin.test.cdi;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;

import com.vaadin.cdi.CDINavigator;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.UI;

@Dependent
@Default
public class MyCDINavigator extends CDINavigator {

    @Override
    public void init(UI ui, ViewDisplay display) {
        // Let the FW Navigator choose the StateManager
        init(ui, null, display);
    }
}
