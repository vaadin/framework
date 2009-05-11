/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.util;

import java.io.File;

import com.vaadin.Application;
import com.vaadin.terminal.SystemError;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 * Provides sample directory based on application directory. If this fails then
 * sampleDirectory property is read. If no sample directory is resolved, then a
 * panel displaying error message is added to main window.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class SampleDirectory {

    /**
     * Get sample directory.
     * 
     * @param application
     * @return file pointing to sample directory
     */
    public static File getDirectory(Application application) {
        String errorMessage = "Access to application "
                + "context base directory failed, "
                + "possible security constraint with Application "
                + "Server or Servlet Container.<br />";
        File file = application.getContext().getBaseDirectory();
        if ((file == null) || (!file.canRead())
                || (file.getAbsolutePath() == null)) {
            // cannot access example directory, possible security issue with
            // Application Server or Servlet Container
            // Try to read sample directory from web.xml parameter
            if (application.getProperty("sampleDirectory") != null) {
                file = new File(application.getProperty("sampleDirectory"));
                if ((file != null) && (file.canRead())
                        && (file.getAbsolutePath() != null)) {
                    // Success using property
                    return file;
                }
                // Failure using property
                errorMessage += "Failed also to access sample directory <b>["
                        + application.getProperty("sampleDirectory")
                        + "]</b> defined in <b>sampleDirectory property</b>.";
            } else {
                // Failure using application context base dir, no property set
                errorMessage += "<b>Note: </b>You can set this manually in "
                        + "web.xml by defining " + "sampleDirectory property.";
            }
        } else {
            // Success using application context base dir
            return file;
        }
        // Add failure notification as an Panel to main window
        final Panel errorPanel = new Panel("Demo application error");
        errorPanel.setStyle("strong");
        errorPanel.setComponentError(new SystemError(
                "Cannot provide sample directory"));
        errorPanel.addComponent(new Label(errorMessage, Label.CONTENT_XHTML));
        // Remove all components from applications main window
        application.getMainWindow().getLayout().removeAllComponents();
        // Add error panel
        application.getMainWindow().getLayout().addComponent(errorPanel);
        return null;
    }
}
