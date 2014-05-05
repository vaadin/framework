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

package com.vaadin.server.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.Constants;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServletPortletHelper;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ApplicationConstants;

/**
 * Serves a connector resource from the classpath if the resource has previously
 * been registered by calling
 * {@link LegacyCommunicationManager#registerDependency(String, Class)}. Sending
 * arbitrary files from the classpath is prevented by only accepting resource
 * names that have explicitly been registered. Resources can currently only be
 * registered by including a {@link JavaScript} or {@link StyleSheet} annotation
 * on a Connector class.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class PublishedFileHandler implements RequestHandler {

    /**
     * Writes the connector resource identified by the request URI to the
     * response. If a published resource corresponding to the URI path is not
     * found, writes a HTTP Not Found error to the response.
     */
    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        if (!ServletPortletHelper.isPublishedFileRequest(request)) {
            return false;
        }

        String pathInfo = request.getPathInfo();
        // + 2 to also remove beginning and ending slashes
        String fileName = pathInfo
                .substring(ApplicationConstants.PUBLISHED_FILE_PATH.length() + 2);

        final String mimetype = response.getService().getMimeType(fileName);

        // Security check: avoid accidentally serving from the UI of the
        // classpath instead of relative to the context class
        if (fileName.startsWith("/")) {
            getLogger().warning(
                    "Published file request starting with / rejected: "
                            + fileName);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, fileName);
            return true;
        }

        // Check that the resource name has been registered
        session.lock();
        Class<?> context;
        try {
            context = session.getCommunicationManager().getDependencies()
                    .get(fileName);
        } finally {
            session.unlock();
        }

        // Security check: don't serve resource if the name hasn't been
        // registered in the map
        if (context == null) {
            getLogger().warning(
                    "Rejecting published file request for file that has not been published: "
                            + fileName);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, fileName);
            return true;
        }

        // Resolve file relative to the location of the context class
        InputStream in = context.getResourceAsStream(fileName);
        if (in == null) {
            getLogger().warning(
                    fileName + " published by " + context.getName()
                            + " not found. Verify that the file "
                            + context.getPackage().getName().replace('.', '/')
                            + '/' + fileName
                            + " is available on the classpath.");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, fileName);
            return true;
        }

        // Set caching for the published file
        String cacheControl = "public, max-age=0, must-revalidate";
        int resourceCacheTime = request.getService()
                .getDeploymentConfiguration().getResourceCacheTime();
        if (resourceCacheTime > 0) {
            cacheControl = "max-age=" + String.valueOf(resourceCacheTime);
        }
        response.setHeader("Cache-Control", cacheControl);

        OutputStream out = null;
        try {
            if (mimetype != null) {
                response.setContentType(mimetype);
            }

            out = response.getOutputStream();

            final byte[] buffer = new byte[Constants.DEFAULT_BUFFER_SIZE];

            int bytesRead = 0;
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                // Do nothing
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }

        return true;
    }

    private static final Logger getLogger() {
        return Logger.getLogger(PublishedFileHandler.class.getName());
    }
}
