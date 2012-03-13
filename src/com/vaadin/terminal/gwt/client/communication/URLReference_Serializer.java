/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ConnectorMap;

public class URLReference_Serializer implements JSONSerializer<URLReference> {

    public URLReference deserialize(JSONObject jsonValue,
            ConnectorMap idMapper, ApplicationConnection connection) {
        URLReference reference = GWT.create(URLReference.class);
        JSONArray jsonURL = (JSONArray) jsonValue.get("URL");
        String URL = (String) JsonDecoder.decodeValue(jsonURL, idMapper,
                connection);
        reference.setURL(connection.translateVaadinUri(URL));
        return reference;
    }

    public JSONObject serialize(URLReference value, ConnectorMap idMapper,
            ApplicationConnection connection) {
        JSONObject json = new JSONObject();
        json.put("URL",
                JsonEncoder.encode(value.getURL(), idMapper, connection));
        return json;
    }

}
