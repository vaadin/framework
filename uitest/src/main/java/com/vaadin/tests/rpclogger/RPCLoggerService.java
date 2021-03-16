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
package com.vaadin.tests.rpclogger;

import java.util.List;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public class RPCLoggerService extends VaadinServletService {

    public RPCLoggerService(VaadinServlet servlet,
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        super(servlet, deploymentConfiguration);
    }

    @Override
    protected List<RequestHandler> createRequestHandlers()
            throws ServiceException {
        List<RequestHandler> handlers = super.createRequestHandlers();
        handlers.add(new LoggingUidlRequestHandler());
        return handlers;
    }
}
