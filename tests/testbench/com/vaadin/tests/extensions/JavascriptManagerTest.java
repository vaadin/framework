/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.extensions;

import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.external.json.JSONObject;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptCallback;

public class JavascriptManagerTest extends AbstractTestRoot {

    private Log log = new Log(5);

    @Override
    protected void setup(WrappedRequest request) {
        addComponent(log);
        final JavaScript js = JavaScript.getCurrent();
        js.addCallback("testing.doTest", new JavaScriptCallback() {
            public void call(JSONArray arguments) throws JSONException {
                log.log("Got " + arguments.length() + " arguments");
                log.log("Argument 1 as a number: " + arguments.getInt(0));
                log.log("Argument 2 as a string: " + arguments.getString(1));
                log.log("Argument 3.p as a boolean: "
                        + arguments.getJSONObject(2).getBoolean("p"));
                log.log("Argument 4 is JSONObject.NULL: "
                        + (arguments.get(3) == JSONObject.NULL));
                js.removeCallback("testing.doTest");
            }
        });
        js.execute("window.testing.doTest(42, 'text', {p: true}, null)");
    }

    @Override
    protected String getTestDescription() {
        return "Test javascript callback handling by adding a callback and invoking the javascript.";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
