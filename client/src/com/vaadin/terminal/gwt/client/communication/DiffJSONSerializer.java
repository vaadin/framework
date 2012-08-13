/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

import com.google.gwt.json.client.JSONValue;
import com.vaadin.terminal.gwt.client.ApplicationConnection;

public interface DiffJSONSerializer<T> extends JSONSerializer<T> {
    /**
     * Update the target object in place based on the passed JSON data.
     * 
     * @param target
     * @param jsonValue
     * @param connection
     */
    public void update(T target, Type type, JSONValue jsonValue,
            ApplicationConnection connection);
}
