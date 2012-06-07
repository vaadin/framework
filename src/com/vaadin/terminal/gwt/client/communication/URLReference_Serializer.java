/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.vaadin.terminal.gwt.client.ApplicationConnection;

public class URLReference_Serializer implements JSONSerializer<URLReference> {

    // setURL() -> uRL as first char becomes lower case...
    private static final String URL_FIELD = "uRL";

    public URLReference deserialize(Type type, JSONValue jsonValue,
            ApplicationConnection connection) {
        URLReference reference = GWT.create(URLReference.class);
        JSONObject json = (JSONObject) jsonValue;
        if (json.containsKey(URL_FIELD)) {
            JSONValue jsonURL = json.get(URL_FIELD);
            String URL = (String) JsonDecoder.decodeValue(
                    new Type(String.class.getName(), null), jsonURL, null,
                    connection);
            reference.setURL(connection.translateVaadinUri(URL));
        }
        return reference;
    }

    public JSONValue serialize(URLReference value,
            ApplicationConnection connection) {
        JSONObject json = new JSONObject();
        json.put(URL_FIELD,
                JsonEncoder.encode(value.getURL(), true, connection));
        return json;
    }

}
