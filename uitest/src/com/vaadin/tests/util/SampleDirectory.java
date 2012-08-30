/* 
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tests.util;

import java.io.File;

import com.vaadin.Application;
import com.vaadin.server.SystemError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

/**
 * Provides sample directory based on application directory. If this fails then
 * sampleDirectory property is read. If no sample directory is resolved, then a
 * panel displaying error message is added to main window.
 * 
 * @author Vaadin Ltd.
 * 
 */
public class SampleDirectory {

    /**
     * Get sample directory.
     * 
     * @param application
     * @return file pointing to sample directory
     */
    public static File getDirectory(Application application, UI uI) {
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
        errorPanel.setStyleName("strong");
        errorPanel.setComponentError(new SystemError(
                "Cannot provide sample directory"));
        errorPanel.addComponent(new Label(errorMessage, ContentMode.XHTML));
        // Remove all components from applications main window
        uI.getContent().removeAllComponents();
        // Add error panel
        uI.getContent().addComponent(errorPanel);
        return null;
    }
}
