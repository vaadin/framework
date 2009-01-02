/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.featurebrowser;

import com.itmill.toolkit.ui.Window;

public class FeaturesApplication extends com.itmill.toolkit.Application {

    @Override
    public void init() {
        if (getProperty("statistics") != null) {
            FeatureUtil.setStatistics(true);
        }
        setUser(new Long(System.currentTimeMillis()).toString());
        final Window main = new Window("IT Mill Toolkit Features Tour");
        setMainWindow(main);

        main.setLayout(new FeatureBrowser());
    }

    /**
     * ErrorEvents are printed to default error stream and not in GUI.
     */
    @Override
    public void terminalError(
            com.itmill.toolkit.terminal.Terminal.ErrorEvent event) {
        final Throwable e = event.getThrowable();
        FeatureUtil.debug(getUser().toString(), "terminalError: "
                + e.toString());
        e.printStackTrace();
    }

}
