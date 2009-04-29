/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.automatedtests;

import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

@SuppressWarnings("serial")
public class SimplestApplication extends com.itmill.toolkit.Application {

    @Override
    public void init() {
        final Window main = new Window("Simplest Application window");
        setMainWindow(main);
        main.addComponent(new Label("Simplest Application label"));
    }
}
