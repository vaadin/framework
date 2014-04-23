/* 
 * Copyright 2000-2014 Vaadin Ltd.
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

import com.vaadin.server.SystemError;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

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
    public static File getDirectory(VaadinSession application, LegacyWindow uI) {
        String errorMessage = "Access to application "
                + "context base directory failed, "
                + "possible security constraint with Application "
                + "Server or Servlet Container.<br />";
        File file = VaadinService.getCurrent().getBaseDirectory();
        if ((file == null) || (!file.canRead())
                || (file.getAbsolutePath() == null)) {
            // cannot access example directory, possible security issue with
            // Application Server or Servlet Container
            // Try to read sample directory from web.xml parameter
            String sampleDirProperty = application.getConfiguration()
                    .getInitParameters().getProperty("sampleDirectory");
            if (sampleDirProperty != null) {
                file = new File(sampleDirProperty);
                if ((file != null) && (file.canRead())
                        && (file.getAbsolutePath() != null)) {
                    // Success using property
                    return file;
                }
                // Failure using property
                errorMessage += "Failed also to access sample directory <b>["
                        + sampleDirProperty
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
        VerticalLayout errorLayout = new VerticalLayout();
        errorLayout.setMargin(true);
        final Panel errorPanel = new Panel("Demo application error",
                errorLayout);
        errorPanel.setStyleName("strong");
        errorPanel.setComponentError(new SystemError(
                "Cannot provide sample directory"));
        errorLayout.addComponent(new Label(errorMessage, ContentMode.HTML));
        // Remove all components from applications main window
        uI.removeAllComponents();
        // Add error panel
        uI.addComponent(errorPanel);
        return null;
    }
}
