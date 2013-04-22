/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.client.debug.internal;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.Console;
import com.vaadin.client.Util;
import com.vaadin.client.ValueMap;
import com.vaadin.client.ui.VNotification;

/**
 * Internal API, do not use. Implements the 'old' Console API and passes the
 * messages to the new debug window.
 * <p>
 * <em>WILL BE CHANGED/REMOVED.</em>
 * </p>
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class ConsoleAdapter implements Console {

    static VDebugWindow window = GWT.create(VDebugWindow.class);

    static {
        window.addSection((Section) GWT.create(LogSection.class));
        window.addSection((Section) GWT.create(HierarchySection.class));
        window.addSection((Section) GWT.create(NetworkSection.class));

    }

    @Override
    public void log(String msg) {
        getLogger().log(Level.INFO, msg);
    }

    @Override
    public void log(Throwable e) {
        if (e instanceof UmbrellaException) {
            UmbrellaException ue = (UmbrellaException) e;
            for (Throwable t : ue.getCauses()) {
                log(t);
            }
            return;
        }
        log(Util.getSimpleName(e) + ": " + e.getMessage());
        GWT.log(e.getMessage(), e);
    }

    @Override
    public void error(Throwable e) {
        handleError(e, this);
    }

    @Override
    public void error(String msg) {
        if (msg == null) {
            msg = "null";
        }

        getLogger().log(Level.SEVERE, msg);
    }

    @Override
    public void printObject(Object msg) {
        String str;
        if (msg == null) {
            str = "null";
        } else {
            str = msg.toString();
        }
        log(str);
        consoleLog(str);
    }

    @Override
    public void dirUIDL(ValueMap u, ApplicationConnection client) {
        window.uidl(client, u);
    }

    @Override
    public void printLayoutProblems(ValueMap meta,
            ApplicationConnection applicationConnection,
            Set<ComponentConnector> zeroHeightComponents,
            Set<ComponentConnector> zeroWidthComponents) {

        window.meta(applicationConnection, meta);
    }

    private boolean quietMode = false;

    @Override
    public void setQuietMode(boolean quietMode) {
        this.quietMode = quietMode;
        if (quietMode) {
            window.close();
        } else {
            // NOP can't switch ATM
        }
    }

    @Override
    public void init() {
        if (!quietMode) {
            window.init();
        }
    }

    static void handleError(Throwable e, Console target) {
        if (e instanceof UmbrellaException) {
            UmbrellaException ue = (UmbrellaException) e;
            for (Throwable t : ue.getCauses()) {
                target.error(t);
            }
            return;
        }
        String exceptionText = Util.getSimpleName(e);
        String message = e.getMessage();
        if (message != null && message.length() != 0) {
            exceptionText += ": " + e.getMessage();
        }
        target.error(exceptionText);
        GWT.log(e.getMessage(), e);
        if (!GWT.isProdMode()) {
            e.printStackTrace();
        }
        try {
            Widget owner = null;

            if (!ApplicationConfiguration.getRunningApplications().isEmpty()) {
                // Make a wild guess and use the first available
                // ApplicationConnection. This is better than than leaving the
                // exception completely unstyled...
                ApplicationConnection connection = ApplicationConfiguration
                        .getRunningApplications().get(0);
                owner = connection.getUIConnector().getWidget();
            }
            VNotification
                    .createNotification(VNotification.DELAY_FOREVER, owner)
                    .show("<h1>Uncaught client side exception</h1><br />"
                            + exceptionText, VNotification.CENTERED, "error");
        } catch (Exception e2) {
            // Just swallow this exception
        }
    }

    private static native void consoleLog(String msg)
    /*-{
         if($wnd.console && $wnd.console.log) {
             $wnd.console.log(msg);
         }
     }-*/;

    private static native void consoleErr(String msg)
    /*-{
         if($wnd.console) {
             if ($wnd.console.error)
                 $wnd.console.error(msg);
             else if ($wnd.console.log)
                 $wnd.console.log(msg);
         }
     }-*/;

    private static Logger getLogger() {
        return Logger.getLogger(ConsoleAdapter.class.getName());
    }
}
