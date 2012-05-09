/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ConnectorMap;

public class URLReference_Serializer implements JSONSerializer<URLReference> {

    public URLReference deserialize(JSONValue jsonValue, URLReference target,
            ConnectorMap idMapper, ApplicationConnection connection) {
        URLReference reference = GWT.create(URLReference.class);
        JSONObject json = (JSONObject) jsonValue;
        if (json.containsKey("URL")) {
            JSONArray jsonURL = (JSONArray) json.get("URL");
            String URL = (String) JsonDecoder.decodeValue(jsonURL, null,
                    idMapper, connection);
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
