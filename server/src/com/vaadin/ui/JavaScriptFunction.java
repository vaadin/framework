/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.terminal.AbstractJavaScriptExtension;

/**
 * Defines a method that is called by a client-side JavaScript function. When
 * the corresponding JavaScript function is called, the {@link #call(JSONArray)}
 * method is invoked.
 * 
 * @see JavaScript#addFunction(String, JavaScriptCallback)
 * @see AbstractJavaScriptComponent#addFunction(String, JavaScriptCallback)
 * @see AbstractJavaScriptExtension#addFunction(String, JavaScriptCallback)
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public interface JavaScriptFunction extends Serializable {
    /**
     * Invoked whenever the corresponding JavaScript function is called in the
     * browser.
     * <p>
     * Because of the asynchronous nature of the communication between client
     * and server, no return value can be sent back to the browser.
     * 
     * @param arguments
     *            an array with JSON representations of the arguments with which
     *            the JavaScript function was called.
     * @throws JSONException
     *             if the arguments can not be interpreted
     */
    public void call(JSONArray arguments) throws JSONException;
}
