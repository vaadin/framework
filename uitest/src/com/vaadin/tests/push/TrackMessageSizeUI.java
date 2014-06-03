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

package com.vaadin.tests.push;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletService;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.JavaScriptFunction;

// Load vaadinPush.js so that jQueryVaadin is defined 
@JavaScript("vaadin://vaadinPush.debug.js")
public class TrackMessageSizeUI extends AbstractTestUIWithLog {

    private String testMethod = "function testSequence(expected, data) {\n"
            + "    var request = {trackMessageLength: true, messageDelimiter: '|'};\n"
            + "    _request = {trackMessageLength: true, messageDelimiter: '|'};\n"
            + "    _handleProtocol = function(a,b) {return true;};"
            + "    var response = {partialMessage: ''};\n"
            + "    var messages = [];\n"
            + "    for(var i = 0; i < data.length; i++) {\n"
            + "        if (!_trackMessageSize(data[i], request, response))\n"
            + "            messages = messages.concat(response.messages);\n"
            + "    }\n"
            + "    if (JSON.stringify(expected) != JSON.stringify(messages)) {\n"
            + "        if (console && typeof console.error == 'function') console.error('Expected', expected, 'but got', messages, 'for', data);\n"
            + "        logToServer('Test failed, see javascript console for details.');\n"
            + "    }" + "}\n";

    @Override
    protected void setup(VaadinRequest request) {
        String methodImplementation = findMethodImplementation();
        getPage().getJavaScript().addFunction("logToServer",
                new JavaScriptFunction() {
                    @Override
                    public void call(JSONArray arguments) throws JSONException {
                        String message = arguments.getString(0);
                        log(message);
                    }
                });

        getPage().getJavaScript().execute(
                methodImplementation + testMethod + buildTestCase());
    }

    private String buildTestCase() {
        // Could maybe express the cases in java and generate JS?
        return "testSequence(['a', 'b'], ['1|a1|b', '']);\n"
                + "testSequence(['a', 'b'], ['1|a1|', 'b']);\n"
                + "testSequence(['a', 'b'], ['1|a1', '|b']);\n"
                + "testSequence(['a', 'b'], ['1|a', '1|b']);\n"
                + "testSequence(['a', 'b'], ['1|a', '', '1|b']);\n"
                + "testSequence(['a|', '|b'], ['2|a|2||b']);\n"
                + "testSequence(['a|', 'b'], ['2|a|', '', '1|b']);\n"
                + "testSequence(['a|', 'b'], ['2|a|', '1|b']);\n"
                + "testSequence(['a|', 'b'], ['2|a|1', '|b']);\n"
                + "testSequence(['a|', 'b'], ['2|a|1|', 'b']);\n"
                + "testSequence([' ', 'b'], ['1| 1|b']);\n"
                + "testSequence([' ', 'b'], ['1| ','1|b']);\n"
                + "testSequence([' ', 'b'], ['1|',' 1|b']);\n"
                + "logToServer('All tests run')\n";
    }

    private String findMethodImplementation() {
        String filename = "/VAADIN/vaadinPush.debug.js";
        URL resourceURL = findResourceURL(filename,
                (VaadinServletService) VaadinService.getCurrent());
        if (resourceURL == null) {
            log("Can't find " + filename);
            return null;
        }

        try {
            String string = IOUtils.toString(resourceURL);

            // Find the function inside the script content
            int startIndex = string.indexOf("function _trackMessageSize");
            if (startIndex == -1) {
                log("function not found");
                return null;
            }

            // Assumes there's a /** comment before the next function
            int endIndex = string.indexOf("/**", startIndex);
            if (endIndex == -1) {
                log("End of function not found");
                return null;
            }

            string = string.substring(startIndex, endIndex);
            string = string.replaceAll("jQuery", "jQueryVaadin");
            return string;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL findResourceURL(String filename, VaadinServletService service) {
        ServletContext sc = service.getServlet().getServletContext();
        URL resourceUrl;
        try {
            resourceUrl = sc.getResource(filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        if (resourceUrl == null) {
            // try if requested file is found from classloader

            // strip leading "/" otherwise stream from JAR wont work
            if (filename.startsWith("/")) {
                filename = filename.substring(1);
            }

            resourceUrl = service.getClassLoader().getResource(filename);
        }
        return resourceUrl;
    }

    @Override
    protected String getTestDescription() {
        return "Unit tests for _trackMessageSize in vaadinPush.debug.js. Implemented with testbench and a full Vaadin server side since the testing requires some file mangling.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(12468);
    }

}
