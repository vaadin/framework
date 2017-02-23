/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.apache.commons.io.IOUtils;

import com.google.gwt.dev.shell.CheckForUpdates;
import com.vaadin.shared.Version;

public class ReportUsage {

    public static final String ANONYMOUS_ID = loadFirstLaunch();

    // for compatibility with old checks
    private static final String USER_AGENT_BASE = "GWT Freshness Checker";

    private static final String COMPILER = "Compiler"; //$NON-NLS-1$

    private static final String E_QPARAM = "&e="; //$NON-NLS-1$

    private static final String R_QPARAM = "&r=unknown"; //$NON-NLS-1$

    private static final String ID_QPARAM = "&id="; //$NON-NLS-1$

    private static final String V_QPARAM = "?v="; //$NON-NLS-1$

    private static final String USER_AGENT = "User-Agent"; //$NON-NLS-1$

    // Use the GWT Freshness checker URL to store usage reports.
    private static final String QUERY_URL = "https://tools.vaadin.com/version/currentversion.xml"; //$NON-NLS-1$

    // Preferences keys
    private static final String FIRST_LAUNCH = "firstLaunch"; //$NON-NLS-1$
    private static final String LAST_PING = "lastPing";

    public static final long ONE_DAY = 24 * 60 * 60 * 1000;

    // for testing only
    public static void main(String[] args) {
        report();
    }

    public static FutureTask<Void> checkForUpdatesInBackgroundThread() {
        FutureTask<Void> task = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ReportUsage.report();
                return null;
            }
        });
        Thread checkerThread = new Thread(task, "Vaadin Update Checker");
        checkerThread.setDaemon(true);
        checkerThread.start();
        return task;
    }

    public static void report() {
        long currentTimeMillis = System.currentTimeMillis();
        Preferences prefs = Preferences.userNodeForPackage(ReportUsage.class);
        String lastPing = prefs.get(LAST_PING, "0");
        if (lastPing != null) {
            try {
                long lastPingTime = Long.parseLong(lastPing);
                if (currentTimeMillis < lastPingTime + ONE_DAY) {
                    return;
                }
            } catch (NumberFormatException e) {
                // error parsing last ping time, ignore and ping
            }
        }

        StringBuilder url = new StringBuilder(QUERY_URL);
        url.append(V_QPARAM);
        url.append(Version.getFullVersion());
        url.append(ID_QPARAM);
        url.append(ANONYMOUS_ID).append(R_QPARAM);

        // TODO add more relevant entry point if feasible
        String entryPoint = COMPILER;

        if (entryPoint != null) {
            url.append(E_QPARAM).append(entryPoint);
        }

        doHttpGet(makeUserAgent(), url.toString());

        prefs.put(LAST_PING, String.valueOf(currentTimeMillis));
    }

    private static void doHttpGet(String userAgent, String url) {
        Throwable caught;
        InputStream is = null;
        try {
            URL urlToGet = new URL(url);
            URLConnection conn = urlToGet.openConnection();
            conn.setRequestProperty(USER_AGENT, userAgent);
            is = conn.getInputStream();
            // TODO use the results
            IOUtils.toByteArray(is);
            return;
        } catch (MalformedURLException e) {
            caught = e;
        } catch (IOException e) {
            caught = e;
        } finally {
            IOUtils.closeQuietly(is);
        }

        Logger.getLogger(ReportUsage.class.getName())
                .fine("Caught an exception while executing HTTP query: "
                        + caught.getMessage());
    }

    private static String makeUserAgent() {
        String userAgent = USER_AGENT_BASE;
        StringBuilder extra = new StringBuilder();
        appendUserAgentProperty(extra, "java.vendor");
        appendUserAgentProperty(extra, "java.version");
        appendUserAgentProperty(extra, "os.arch");
        appendUserAgentProperty(extra, "os.name");
        appendUserAgentProperty(extra, "os.version");

        if (extra.length() > 0) {
            userAgent += " (" + extra.toString() + ")";
        }

        return userAgent.toString();
    }

    private static void appendUserAgentProperty(StringBuilder sb,
            String propName) {
        String propValue = System.getProperty(propName);
        if (propValue != null) {
            if (sb.length() > 0) {
                sb.append(';').append(' ');
            }
            sb.append(propName);
            sb.append('=');
            sb.append(propValue);
        }
    }

    private static String loadFirstLaunch() {
        Preferences prefs = Preferences
                .userNodeForPackage(CheckForUpdates.class);

        String firstLaunch = prefs.get(FIRST_LAUNCH, null);
        if (firstLaunch == null) {
            long currentTimeMillis = System.currentTimeMillis();
            firstLaunch = Long.toHexString(currentTimeMillis);
            prefs.put(FIRST_LAUNCH, firstLaunch);
        }
        return firstLaunch;

    }

}
