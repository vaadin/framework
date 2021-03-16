/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.server.communication;

import com.vaadin.server.BootstrapHandler;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public class ServletBootstrapHandler extends BootstrapHandler {
    @Override
    protected String getServiceUrl(BootstrapContext context) {
        String pathInfo = context.getRequest().getPathInfo();
        if (pathInfo == null) {
            return null;
        } else {
            /*
             * Make a relative URL to the servlet by adding one ../ for each
             * path segment in pathInfo (i.e. the part of the requested path
             * that comes after the servlet mapping)
             */
            return VaadinServletService.getCancelingRelativePath(pathInfo);
        }
    }

    @Override
    public String getThemeName(BootstrapContext context) {
        String themeName = context.getRequest()
                .getParameter(VaadinServlet.URL_PARAMETER_THEME);
        if (themeName == null) {
            themeName = super.getThemeName(context);
        }
        return themeName;
    }
}
