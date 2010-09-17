package com.vaadin.terminal.gwt.client;

import java.util.Set;

import com.google.gwt.core.client.GWT;

/**
 * A helper class to do some client side logging.
 * <p>
 * This class replaces previously used loggin style:
 * ApplicationConnection.getConsole().log("foo").
 * <p>
 * The default widgetset provides three modes for debugging:
 * <ul>
 * <li>NullConsole (Default, displays no errors at all)
 * <li>VDebugConsole ( Enabled by appending ?debug to url. Displays a floating
 * console in the browser and also prints to browsers internal console (builtin
 * or Firebug) and GWT's development mode console if available.)
 * <li>VDebugConsole in quiet mode (Enabled by appending ?debug=quiet. Same as
 * previous but without the console floating over application).
 * </ul>
 * <p>
 * Implementations can be customized with GWT deferred binding by overriding
 * NullConsole.class or VDebugConsole.class. This way developer can for example
 * build mechanism to send client side logging data to a server.
 * <p>
 * Note that logging in client side is not fully optimized away even in
 * production mode. Use logging moderately in production code to keep the size
 * of client side engine small. An exception is {@link GWT#log(String)} style
 * logging, which is available only in GWT development mode, but optimized away
 * when compiled to web mode.
 * 
 * 
 * TODO improve javadocs of individual methods
 * 
 */
public class VConsole {
    private static Console impl;

    /**
     * Used by ApplicationConfiguration to initialize VConsole.
     * 
     * @param console
     */
    static void setImplementation(Console console) {
        impl = console;
    }

    /**
     * Used by ApplicationConnection to support deprecated getConsole() api.
     */
    static Console getImplementation() {
        return impl;
    }

    public static void log(String msg) {
        impl.log(msg);
    }

    public static void log(Throwable e) {
        impl.log(e);
    }

    public static void error(Throwable e) {
        impl.error(e);
    }

    public static void error(String msg) {
        impl.error(msg);
    }

    public static void printObject(Object msg) {
        impl.printObject(msg);
    }

    public static void dirUIDL(UIDL u, ApplicationConfiguration cnf) {
        impl.dirUIDL(u, cnf);
    }

    public static void printLayoutProblems(ValueMap meta,
            ApplicationConnection applicationConnection,
            Set<Paintable> zeroHeightComponents,
            Set<Paintable> zeroWidthComponents) {
        impl.printLayoutProblems(meta, applicationConnection,
                zeroHeightComponents, zeroWidthComponents);
    }

}
