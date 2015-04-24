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
package com.vaadin.shared;

import java.io.Serializable;

import com.vaadin.shared.util.SharedUtil;

/**
 * Utility for translating special Vaadin URIs like theme:// and app:// into
 * URLs usable by the browser. This is an abstract class performing the main
 * logic in {@link #resolveVaadinUri(String)} and using abstract methods in the
 * class for accessing information specific to the current environment.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public abstract class VaadinUriResolver implements Serializable {

    /**
     * Translates a Vaadin URI to a URL that can be loaded by the browser. The
     * following URI schemes are supported:
     * <ul>
     * <li><code>theme://</code> - resolves to the URL of the currently active
     * theme.</li>
     * <li><code>published://</code> - resolves to resources on the classpath
     * published by {@link com.vaadin.annotations.JavaScript @JavaScript} or
     * {@link com.vaadin.annotations.StyleSheet @StyleSheet} annotations on
     * connectors.</li>
     * <li><code>app://</code> - resolves to a URL that will be routed to the
     * currently registered {@link com.vaadin.server.RequestHandler
     * RequestHandler} instances.</li>
     * <li><code>vaadin://</code> - resolves to the location of static resouces
     * in the VAADIN directory</li>
     * </ul>
     * Any other URI protocols, such as <code>http://</code> or
     * <code>https://</code> are passed through this method unmodified.
     *
     * @since 7.4
     * @param vaadinUri
     *            the uri to resolve
     * @return the resolved uri
     */
    public String resolveVaadinUri(String vaadinUri) {
        if (vaadinUri == null) {
            return null;
        }
        if (vaadinUri.startsWith(ApplicationConstants.THEME_PROTOCOL_PREFIX)) {
            final String themeUri = getThemeUri();
            vaadinUri = themeUri + vaadinUri.substring(7);
        }

        if (vaadinUri
                .startsWith(ApplicationConstants.PUBLISHED_PROTOCOL_PREFIX)) {
            // getAppUri *should* always end with /
            // substring *should* always start with / (published:///foo.bar
            // without published://)
            vaadinUri = ApplicationConstants.APP_PROTOCOL_PREFIX
                    + ApplicationConstants.PUBLISHED_FILE_PATH
                    + vaadinUri
                            .substring(ApplicationConstants.PUBLISHED_PROTOCOL_PREFIX
                                    .length());
            // Let translation of app:// urls take care of the rest
        }
        if (vaadinUri.startsWith(ApplicationConstants.APP_PROTOCOL_PREFIX)) {
            String relativeUrl = vaadinUri
                    .substring(ApplicationConstants.APP_PROTOCOL_PREFIX
                            .length());
            String serviceUrl = getServiceUrl();
            String serviceUrlParameterName = getServiceUrlParameterName();
            if (serviceUrlParameterName != null) {
                // Should put path in v-resourcePath parameter and append query
                // params to base portlet url
                String[] parts = relativeUrl.split("\\?", 2);
                String path = parts[0];

                // If there's a "?" followed by something, append it as a query
                // string to the base URL
                if (parts.length > 1) {
                    String appUrlParams = parts[1];
                    serviceUrl = SharedUtil.addGetParameters(serviceUrl,
                            appUrlParams);
                }
                if (!path.startsWith("/")) {
                    path = '/' + path;
                }
                String pathParam = serviceUrlParameterName + "="
                        + encodeQueryStringParameterValue(path);
                serviceUrl = SharedUtil.addGetParameters(serviceUrl, pathParam);
                vaadinUri = serviceUrl;
            } else {
                vaadinUri = serviceUrl + relativeUrl;
            }
        }
        if (vaadinUri.startsWith(ApplicationConstants.VAADIN_PROTOCOL_PREFIX)) {
            final String vaadinDirUri = getVaadinDirUrl();
            String relativeUrl = vaadinUri
                    .substring(ApplicationConstants.VAADIN_PROTOCOL_PREFIX
                            .length());
            vaadinUri = vaadinDirUri + relativeUrl;
        }

        return vaadinUri;
    }

    /**
     * Gets the URL pointing to the VAADIN directory.
     * 
     * @return the VAADIN directory URL
     */
    protected abstract String getVaadinDirUrl();

    /**
     * Gets the name of the request parameter that should be used for sending
     * the requested URL to the {@link #getServiceUrl() service URL}. If
     * <code>null</code> is returned, the requested URL will instead be appended
     * to the base service URL.
     * 
     * @return the parameter name used for passing request URLs, or
     *         <code>null</code> to send the path as a part of the request path.
     */
    protected abstract String getServiceUrlParameterName();

    /**
     * Gets the URL handled by {@link com.vaadin.server.VaadinService
     * VaadinService} to handle application requests.
     * 
     * @return the service URL
     */
    protected abstract String getServiceUrl();

    /**
     * Gets the URI of the directory of the current theme.
     * 
     * @return the URI of the current theme directory
     */
    protected abstract String getThemeUri();

    /**
     * Encodes a value for safe inclusion as a parameter in the query string.
     * 
     * @param parameterValue
     *            the value to encode
     * @return the encoded value
     */
    protected abstract String encodeQueryStringParameterValue(
            String parameterValue);
}
