/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license.pdf. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.terminal.web;

/** <p>Class providing centralized logging services. The logger defines
 * five message types, and provides methods to create messages of those
 * types. These types are:</p>
 * 
 * <ul>
 * <li> <code>info</code> - Useful information generated during normal
 * operation of the application
 * <li> <code>warning</code> - An error situation has occurred, but the
 * operation was able to finish succesfully
 * <li> <code>error</code> - An error situation which prevented the
 * operation from finishing succesfully
 * <li> <code>debug</code> - Internal information from the application meant
 * for developers
 * <li> <code>exception</code> - A Java exception reported using the logger.
 * Includes the exception stack trace and a possible free-form message
 * </ul>
 * 
 * <p>Currently the class offers logging only to the standard output</p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Log {

    private static boolean useStdOut = true;
    
    private static String LOG_MSG_INFO   = "[INFO]";
    private static String LOG_MSG_ERROR  = "[ERROR]";
    private static String LOG_MSG_WARN   = "[WARNING]";
    private static String LOG_MSG_DEBUG  = "[DEBUG]";
    private static String LOG_MSG_EXCEPT = "[EXCEPTION]";
            
    /** Logs a <code>warning</code> message.
     * 
     * @param message Message <code>String</code> to be logged.
     */
    protected static synchronized void warn(java.lang.String message) {
        if (Log.useStdOut) System.out.println(LOG_MSG_WARN+ " "+message);
    }    
    /** Logs a <code>debug</code> message.
     * 
     * @param message Message <code>String</code> to be logged.
     */
    protected static synchronized void debug(java.lang.String message) {
        if (Log.useStdOut) System.out.println(LOG_MSG_DEBUG+ " "+message);
    }

    /** Logs an <code>info</code> message.
     * 
     * @param message Message <code>String</code> to be logged.
     */
    protected static synchronized void info(java.lang.String message) {
        if (Log.useStdOut) System.out.println(LOG_MSG_INFO+ " "+message);
    }  
    
    /** Logs a Java exception and an accompanying error message.
     * 
     * @param message Message <code>String</code> to be logged.
     * @param e Exception to be logged.
     */
    protected static synchronized void except(java.lang.String message, Exception e) {
        if (Log.useStdOut) {
            System.out.println(LOG_MSG_EXCEPT+ " "+message);
            e.printStackTrace();
        }
    }
    
    /** Logs an <code>error</code> message.
     * 
     * @param message Message <code>String</code> to be logged.
     */
    protected static synchronized void error(java.lang.String message) {
        if (Log.useStdOut) System.out.println(LOG_MSG_ERROR+ " "+message);
    }      
}
