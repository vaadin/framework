/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.automatedtests;

import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SimplestApplication extends com.vaadin.Application {

    @Override
    public void init() {
        final Window main = new Window("Simplest Application window");
        setMainWindow(main);
        main.addComponent(new Label("Simplest Application label"));
    }
}
