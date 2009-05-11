/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.automatedtests.util;

import java.io.Serializable;
import java.util.HashMap;

import com.vaadin.ui.Component;

public class DebugId implements Serializable {

    private static HashMap debugIds = new HashMap();

    /**
     * Generate static debug id based on package and component type. If
     * duplicate package, component type then number of instances count is
     * appended to debugId.
     * 
     * @param c
     */
    public static void set(Component c, String description) {
        String debugId = "";

        // add package name
        StackTraceElement[] st = new Throwable().fillInStackTrace()
                .getStackTrace();
        try {
            debugId += st[3].getClassName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // add component type
        debugId += c.getClass();

        // add given description
        debugId += description;

        if (debugIds.containsKey(debugId)) {
            int count = ((Integer) debugIds.get(debugId)).intValue();
            count++;
            debugIds.put(debugId, new Integer(count));
            debugId = debugId + "-" + count;
        }

        c.setDebugId(debugId);
    }
}
