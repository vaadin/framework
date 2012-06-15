/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;

public interface JavaScriptCallback extends Serializable {
    public void call(JSONArray arguments) throws JSONException;
}
