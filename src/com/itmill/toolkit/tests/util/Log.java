package com.itmill.toolkit.tests.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * Execution output and error messages should be handled through this class. It
 * is likely that we need these messages back to TT Server at some point just to
 * figure out what went wrong.
 * 
 */
public class Log {

    // 3 (errors only)
    // 2 (+ warnings)
    // 1 (+logs)
    // 0 (all, print messages also to System.out)
    public static final int debug = 0;

    // Should class.method() and it's call times be told on debug?
    public static final boolean showClassInformation = true;

    public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static int DEBUG = 0;

    public static int LOG = 1;

    public static int WARN = 2;

    public static int ERROR = 3;

    // public List messages = new LinkedList();

    private static Log log;

    public static HashMap classMethodCallCounter = new HashMap();

    static {
        log = new Log();
    }

    public static void reset() {
        // log.messages = new LinkedList();
        classMethodCallCounter = new HashMap();
    }

    public static String getNow() {
        return df.format(new Date());
    }

    private Log() {
    }

    public static String getSource() {
        StackTraceElement[] st = new Throwable().fillInStackTrace()
                .getStackTrace();
        try {
            String key = "";
            // Class fromCallerClass = Class.forName(st[4].getClassName());
            // String fromMethodName = st[4].getMethodName();
            // Class callerClass = Class.forName(st[3].getClassName());
            String methodName = st[3].getMethodName();
            int line = st[3].getLineNumber();

            String clazz = st[3].getClassName() + ".java";
            // String clazz = st[3].getClassName().substring(
            // st[3].getClassName().lastIndexOf('.') + 1)
            // + ".java";
            key = "(" + clazz + ":" + line + ")" + " " + methodName;
            Integer value = (Integer) classMethodCallCounter.get(key);
            if (value == null)
                value = new Integer(1);
            else
                value = new Integer(value.intValue() + 1);
            classMethodCallCounter.put(key, value);
            return value.intValue() + ": " + key;
        } catch (Exception e) {
            return "unknown class.method";
        }

        // List stacks = new ArrayList();
        // for (int i = 0; i < st.length; i++) {
        // try {
        // Class callerClass = Class.forName(st[i].getClassName());
        // String methodName = st[i].getMethodName();
        // System.out.println(i + " = " + callerClass + ", " + methodName);
        // stacks.add(callerClass.getSimpleName());
        // } catch (ClassNotFoundException e) {
        // }
        // }
        // System.out.println("\n");
        // return "";

    }

    public static String getClassMethodCounters() {
        String result = "";
        for (final Iterator it = classMethodCallCounter.keySet().iterator(); it
                .hasNext();) {
            String key = (String) it.next();
            result += classMethodCallCounter.get(key) + ": " + key + "\n";
        }
        return result;
    }

    // public String toString() {
    // StringBuffer sb = new StringBuffer(2048);
    // for (final Iterator it = messages.iterator(); it.hasNext();) {
    // Message msg = (Message) it.next();
    // sb.append(msg.toString() + "\n");
    // }
    // return sb.toString();
    // }

    public void add(int type, String message) {
        String source = getSource();
        // log.messages.add(new Message(DEBUG, message, source));
        if (type >= debug) {
            if (showClassInformation)
                System.out.println(source + ": " + message);
            else
                System.out.println(message);
        }
    }

    public static void debug(String message) {
        log.add(DEBUG, message);
    }

    public static void log(String message) {
        log.add(LOG, message);
    }

    public static void warn(String message) {
        log.add(WARN, message);
    }

    public static void error(String message) {
        log.add(ERROR, message);
    }

    public class Message {

        private final long timemillis = System.currentTimeMillis();

        private final int type;

        private final String source;

        private final String message;

        public Message(int type, String message, String source) {
            this.source = source;
            this.type = type;
            this.message = message;
        }

        public String toString() {
            return df.format(new Date(timemillis)) + ";" + source + ";" + type
                    + ";" + message;
        }

    }

    // public List getMessages() {
    // return messages;
    // }

    /**
     * Simple way to check for memory consumption without profiler.
     */
    public static String getMemoryStatistics() {
        // You should call gc before printing statistics (if you are not using a
        // profiler)
        System.gc();
        long inUse = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                .freeMemory());
        System.out.println(inUse);
        return "Memory:\n" + inUse + " (Used)\n"
                + Runtime.getRuntime().totalMemory() + " (Total)\n"
                + Runtime.getRuntime().freeMemory() + " (Free)\n";

    }
}
