/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.RequestHandler;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;

public class ApplicationResourceHandler implements RequestHandler {
    private static final Pattern APP_RESOURCE_PATTERN = Pattern
            .compile("^/?APP/(\\d+)/.*");

    @Override
    public boolean handleRequest(Application application,
            WrappedRequest request, WrappedResponse response)
            throws IOException {
        // Check for application resources
        String requestPath = request.getRequestPathInfo();
        if (requestPath == null) {
            return false;
        }
        Matcher resourceMatcher = APP_RESOURCE_PATTERN.matcher(requestPath);

        if (resourceMatcher.matches()) {
            ApplicationResource resource = application
                    .getResource(resourceMatcher.group(1));
            if (resource != null) {
                DownloadStream stream = resource.getStream();
                if (stream != null) {
                    stream.setCacheTime(resource.getCacheTime());
                    stream.writeTo(response);
                    return true;
                }
            }
            // We get here if the url looks like an application resource but no
            // resource can be served
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    request.getRequestPathInfo() + " can not be found");
            return true;
        }

        return false;
    }
}
