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
package com.vaadin.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.widgetsetutils.WidgetSetBuilder;

/**
 * A wrapper for the GWT compiler that runs the compiler in a new thread after
 * updating the widgetset file.
 * 
 * This class originally existed to allow circumventing a J2SE 5.0 bug (6316197)
 * that prevents setting the stack size for the main thread.
 * 
 * This class takes the same command line arguments as the
 * com.google.gwt.dev.Compiler class.
 * 
 * A typical invocation would use e.g. the following arguments
 * 
 * "-war WebContent/VAADIN/widgetsets com.vaadin.DefaultWidgetSet"
 * 
 * In addition, larger memory usage settings for the VM should be used, e.g.
 * 
 * "-Xms256M -Xmx512M -Xss8M"
 * 
 * The source directory containing widgetset and related classes must be
 * included in the classpath, as well as other relevant JARs.
 * 
 * @deprecated with Java 6, can use com.google.gwt.dev.Compiler directly (also
 *             in Eclipse plug-in etc.)
 */
@Deprecated
public class WidgetsetCompiler {

    /**
     * @param args
     *            same arguments as for com.google.gwt.dev.Compiler
     */
    public static void main(final String[] args) {
        try {
            // run the compiler in a different thread to enable using the
            // user-set stack size

            // on Windows, the default stack size is too small for the main
            // thread and cannot be changed in JRE 1.5 (see
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6316197)

            Runnable runCompiler = new Runnable() {
                @Override
                public void run() {
                    try {
                        // GWTCompiler.main(args);
                        // avoid warnings

                        String wsname = args[args.length - 1];

                        // TODO expecting this is launched via eclipse WTP
                        // project
                        System.out
                                .println("Updating GWT module description file...");
                        WidgetSetBuilder.updateWidgetSet(wsname);
                        System.out.println("Done.");

                        System.out.println("Starting GWT compiler");
                        com.google.gwt.dev.Compiler.main(args);
                    } catch (Throwable thr) {
                        getLogger().log(Level.SEVERE,
                                "Widgetset compilation failed", thr);
                    }
                }
            };
            Thread runThread = new Thread(runCompiler);
            runThread.start();
            runThread.join();
            System.out.println("Widgetset compilation finished");
        } catch (Throwable thr) {
            getLogger().log(Level.SEVERE, "Widgetset compilation failed", thr);
        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(WidgetsetCompiler.class.getName());
    }
}
