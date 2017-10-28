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
package com.vaadin.client;

import com.google.gwt.logging.client.LogConfiguration;
import com.vaadin.client.debug.internal.VDebugWindow;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A helper class to do some client side logging.
 *
 * @deprecated as of 7.1, use {@link Logger} from org.slf4j instead.
 */
@Deprecated
public class VConsole {
    private static VDebugWindow impl;

    /**
     * Used by ApplicationConfiguration to initialize VConsole.
     *
     * @param console
     */
    static void setImplementation(VDebugWindow console) {
        impl = console;
    }

    public static void log(String msg) {
        if (LogConfiguration.loggingIsEnabled(Level.INFO)) {
            // Check for null, so no NullPointerException is generated when
            // formatting (#12588)
            getLogger().info(msg == null ? "null" : msg);
        }
    }

    public static void log(Throwable e) {
        if (LogConfiguration.loggingIsEnabled(Level.INFO)) {
            // Check for null, so no NullPointerException is generated when
            // formatting (#12588)
            getLogger().info(
                    e.getMessage() == null ? "" : e.getMessage(), e);
        }
    }

    public static void error(Throwable e) {
        if (LogConfiguration.loggingIsEnabled(Level.SEVERE)) {
            // Check for null, so no NullPointerException is generated when
            // formatting (#12588)
            getLogger().error(
                    e.getMessage() == null ? "" : e.getMessage(), e);
        }
    }

    public static void error(String msg) {
        if (LogConfiguration.loggingIsEnabled(Level.SEVERE)) {
            // Check for null, so no NullPointerException is generated when
            // formatting (#12588)
            getLogger().error(msg == null ? "null" : msg);
        }
    }

    public static void printObject(Object msg) {
        String str;
        if (msg == null) {
            str = "null";
        } else {
            str = msg.toString();
        }
        log(str);
    }

    public static void dirUIDL(ValueMap u, ApplicationConnection client) {
        if (impl != null) {
            impl.uidl(client, u);
        }
    }

    public static void printLayoutProblems(ValueMap meta,
            ApplicationConnection applicationConnection) {
        if (impl != null) {
            impl.meta(applicationConnection, meta);
        }
    }

    private static org.slf4j.Logger getLogger() {
        return LoggerFactory.getLogger(VConsole.class);
    }
}
