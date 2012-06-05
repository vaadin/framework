/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ConnectorMap;

public class URLReference_Serializer implements JSONSerializer<URLReference> {

    public URLReference deserialize(Type type, JSONValue jsonValue,
            ApplicationConnection connection) {
        URLReference reference = GWT.create(URLReference.class);
        JSONObject json = (JSONObject) jsonValue;
        if (json.containsKey("URL")) {
            JSONValue jsonURL = json.get("URL");
            String URL = (String) JsonDecoder.decodeValue(
                    new Type(String.class.getName(), null), jsonURL, null,
                    connection);
            reference.setURL(connection.translateVaadinUri(URL));
        }
        return reference;
    }

    public JSONValue serialize(URLReference value, ConnectorMap idMapper,
            ApplicationConnection connection) {
        JSONObject json = new JSONObject();
        json.put("URL",
                JsonEncoder.encode(value.getURL(), true, idMapper, connection));
        return json;
    }

}
