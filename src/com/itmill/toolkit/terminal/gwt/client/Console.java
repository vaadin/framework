/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import java.util.Set;

import com.google.gwt.json.client.JSONArray;

public interface Console {

    public abstract void log(String msg);

    public abstract void error(String msg);

    public abstract void printObject(Object msg);

    public abstract void dirUIDL(UIDL u);

    public abstract void printLayoutProblems(JSONArray array,
            ApplicationConnection applicationConnection,
            Set<Paintable> zeroHeightComponents,
            Set<Paintable> zeroWidthComponents);

}