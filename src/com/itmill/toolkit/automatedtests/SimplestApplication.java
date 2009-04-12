/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.automatedtests;

import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

public class SimplestApplication extends com.itmill.toolkit.Application {
    private static final long serialVersionUID = 1401107566407830534L;

    @Override
    public void init() {
        final Window main = new Window("Simplest Application window");
        setMainWindow(main);
        main.addComponent(new Label("Simplest Application label"));
    }
}
