/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.application;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.communication.HeartbeatHandler;
import com.vaadin.server.communication.UidlRequestHandler;
import com.vaadin.ui.UI;

public class CommErrorEmulatorServlet extends VaadinServlet {

    private Map<UI, Integer> uidlResponseCode = Collections
            .synchronizedMap(new HashMap<UI, Integer>());
    private Map<UI, Integer> heartbeatResponseCode = Collections
            .synchronizedMap(new HashMap<UI, Integer>());

    private final CommErrorUIDLRequestHandler uidlHandler = new CommErrorUIDLRequestHandler();
    private final CommErrorHeartbeatHandler heartbeatHandler = new CommErrorHeartbeatHandler();

    public class CommErrorUIDLRequestHandler extends UidlRequestHandler {
        @Override
        public boolean synchronizedHandleRequest(VaadinSession session,
                VaadinRequest request, VaadinResponse response)
                throws IOException {
            UI ui = session.getService().findUI(request);
            if (ui != null && uidlResponseCode.containsKey(ui)) {
                response.sendError(uidlResponseCode.get(ui), "Error set in UI");
                return true;
            }

            return super.synchronizedHandleRequest(session, request, response);
        }
    }

    public class CommErrorHeartbeatHandler extends HeartbeatHandler {
        @Override
        public boolean synchronizedHandleRequest(VaadinSession session,
                VaadinRequest request, VaadinResponse response)
                throws IOException {
            UI ui = session.getService().findUI(request);
            if (ui != null && heartbeatResponseCode.containsKey(ui)) {
                response.sendError(heartbeatResponseCode.get(ui),
                        "Error set in UI");
                return true;
            }

            return super.synchronizedHandleRequest(session, request, response);
        }

    }

    public class CommErrorEmulatorService extends VaadinServletService {

        public CommErrorEmulatorService(VaadinServlet servlet,
                DeploymentConfiguration deploymentConfiguration)
                throws ServiceException {
            super(servlet, deploymentConfiguration);
        }

        @Override
        protected List<RequestHandler> createRequestHandlers()
                throws ServiceException {
            List<RequestHandler> handlers = super.createRequestHandlers();
            handlers.add(uidlHandler);
            handlers.add(heartbeatHandler);
            return handlers;
        }
    }

    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        CommErrorEmulatorService s = new CommErrorEmulatorService(this,
                deploymentConfiguration);
        s.init();
        return s;
    }

    public void setUIDLResponseCode(final UI ui, int responseCode,
            final int delay) {
        uidlResponseCode.put(ui, responseCode);
        System.out.println(
                "Responding with " + responseCode + " to UIDL requests for "
                        + ui + " for the next " + delay + "s");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Handing UIDL requests normally again");

                uidlResponseCode.remove(ui);
            }
        }).start();
    }

    public void setHeartbeatResponseCode(final UI ui, int responseCode,
            final int delay) {
        heartbeatResponseCode.put(ui, responseCode);

        System.out.println("Responding with " + responseCode
                + " to heartbeat requests for " + ui + " for the next " + delay
                + "s");

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Handing heartbeat requests normally again");
                heartbeatResponseCode.remove(ui);
            }
        }).start();
    }

}
