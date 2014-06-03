/* 
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tests.extensions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;

public class JavascriptManagerTest extends AbstractTestUI {

    private Log log = new Log(5);

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(log);
        final JavaScript js = JavaScript.getCurrent();
        js.addFunction("testing.doTest", new JavaScriptFunction() {
            @Override
            public void call(JSONArray arguments) throws JSONException {
                log.log("Got " + arguments.length() + " arguments");
                log.log("Argument 1 as a number: " + arguments.getInt(0));
                log.log("Argument 2 as a string: " + arguments.getString(1));
                log.log("Argument 3.p as a boolean: "
                        + arguments.getJSONObject(2).getBoolean("p"));
                log.log("Argument 4 is JSONObject.NULL: "
                        + (arguments.get(3) == JSONObject.NULL));
                js.removeFunction("testing.doTest");
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
