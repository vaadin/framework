package com.vaadin.launcher;

import java.lang.reflect.Method;

/**
 * A wrapper for the GWT 1.6 compiler that runs the compiler in a new thread.
 * 
 * This allows circumventing a J2SE 5.0 bug (6316197) that prevents setting the
 * stack size for the main thread. Thus, larger widgetsets can be compiled.
 * 
 * This class takes the same command line arguments as the
 * com.google.gwt.dev.GWTCompiler class. The old and deprecated compiler is used
 * for compatibility with GWT 1.5.
 * 
 * A typical invocation would use e.g. the following arguments
 * 
 * "-out WebContent/VAADIN/widgetsets com.vaadin.terminal.gwt.DefaultWidgetSet"
 * 
 * In addition, larger memory usage settings for the VM should be used, e.g.
 * 
 * "-Xms256M -Xmx512M -Xss8M"
 * 
 * The source directory containing widgetset and related classes must be
 * included in the classpath, as well as the gwt-dev-[platform].jar and other
 * relevant JARs.
 */
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
                public void run() {
                    try {
                        // GWTCompiler.main(args);
                        Class<?> compilerClass = Class
                                .forName("com.google.gwt.dev.GWTCompiler");
                        Method method = compilerClass.getDeclaredMethod("main",
                                String[].class);
                        method.invoke(null, new Object[] { args });
                    } catch (Throwable thr) {
                        thr.printStackTrace();
                    }
                }
            };
            Thread runThread = new Thread(runCompiler);
            runThread.start();
            runThread.join();
            System.out.println("Widgetset compilation finished");
        } catch (Throwable thr) {
            thr.printStackTrace();
        }
    }
}
