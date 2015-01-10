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

public abstract class VaadinUriResolver implements Serializable {

    public String resolveVaadinUri(String vaadinUri) {
        if (vaadinUri == null) {
            return null;
        }
        if (vaadinUri.startsWith("theme://")) {
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

    protected abstract String getVaadinDirUrl();

    protected abstract String getServiceUrlParameterName();

    protected abstract String getServiceUrl();

    protected abstract String getThemeUri();

    protected abstract String encodeQueryStringParameterValue(String queryString);
}
