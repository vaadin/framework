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

package com.vaadin.launcher.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class opens default browser for DemoLauncher class. Default browser is
 * detected by the operating system.
 * 
 */
public class BrowserLauncher {

    /**
     * Open browser on specified URL.
     * 
     * @param url
     */
    public static void openBrowser(String url) {

        final Runtime runtime = Runtime.getRuntime();
        boolean started = false;

        final String os = System.getProperty("os.name", "windows")
                .toLowerCase();

        // Linux
        if (os.indexOf("linux") >= 0) {
            // See if the default browser is Konqueror by resolving the symlink.
            boolean isDefaultKonqueror = false;
            try {
                // Find out the location of the x-www-browser link from path.
                Process process = runtime.exec("which x-www-browser");
                BufferedInputStream ins = new BufferedInputStream(
                        process.getInputStream());
                BufferedReader bufreader = new BufferedReader(
                        new InputStreamReader(ins));
                String defaultLinkPath = bufreader.readLine();
                ins.close();

                // The path is null if the link did not exist.
                if (defaultLinkPath != null) {
                    // See if the default browser is Konqueror.
                    File file = new File(defaultLinkPath);
                    String canonical = file.getCanonicalPath();
                    if (canonical.indexOf("konqueror") != -1) {
                        isDefaultKonqueror = true;
                    }
                }
            } catch (IOException e1) {
                // The symlink was probably not found, so this is ok.
            }

            // Try x-www-browser, which is symlink to the default browser,
            // except if we found that it is Konqueror.
            if (!started && !isDefaultKonqueror) {
                try {
                    runtime.exec("x-www-browser " + url);
                    started = true;
                } catch (final IOException e) {
                }
            }

            // Try firefox
            if (!started) {
                try {
                    runtime.exec("firefox " + url);
                    started = true;
                } catch (final IOException e) {
                }
            }

            // Try mozilla
            if (!started) {
                try {
                    runtime.exec("mozilla " + url);
                    started = true;
                } catch (final IOException e) {
                }
            }

            // Try konqueror
            if (!started) {
                try {
                    runtime.exec("konqueror " + url);
                    started = true;
                } catch (final IOException e) {
                }
            }
        }

        // OS X
        if (os.indexOf("mac os x") >= 0) {

            // Try open
            if (!started) {
                try {
                    runtime.exec("open " + url);
                    started = true;
                } catch (final IOException e) {
                }
            }
        }

        // Try cmd /start command on windows
        if (os.indexOf("win") >= 0) {
            if (!started) {
                try {
                    runtime.exec("cmd /c start " + url);
                    started = true;
                } catch (final IOException e) {
                }
            }
        }

        if (!started) {
            System.out.println("Failed to open browser. Please go to " + url);
        }
    }

}
