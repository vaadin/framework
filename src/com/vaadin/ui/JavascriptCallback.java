/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.external.json.JSONArray;

public interface JavascriptCallback extends Serializable {
    public void call(JSONArray arguments);
}
