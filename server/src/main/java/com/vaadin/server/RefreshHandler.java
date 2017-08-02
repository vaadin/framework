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
package com.vaadin.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.UI;

/**
 * Maintains a hanging request and writes a response either when a predetermined
 * time is up (default 5 minutes) or when the servlet closes down. When the
 * client issues a new request, it can detect that the server has been restarted
 * and thus reload the application.
 * <p>
 * Only requests from localhost are handled - this is a tool mainly used for
 * making local development more convenient.
 *
 * @since
 * @author Vaadin Ltd
 */
public class RefreshHandler implements RequestHandler {

    /*
     * The token changes every time the refreshhandler is reloaded. This should
     * only happen during servlet context reload, i.e. when the servlet is shut
     * down and restarted.
     */
    private final String token = Integer
            .toString(ThreadLocalRandom.current().nextInt());

    private static final Set<String> localhostAddresses = new HashSet<>(
            Arrays.asList("0:0:0:0:0:0:0:1", "fe80:0:0:0:0:0:0:1", "127.0.0.1",
                    "::1"));

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {

        if (!ServletPortletHelper.isRefreshRequest(request)) {
            // Not a request for us
            return false;
        }

        String remoteAddr = request.getRemoteAddr();
        // replaceAll removes %0 or similar that identify which network
        // interface is used
        if (!localhostAddresses
                .contains(remoteAddr.replaceAll("%[0-9]+$", ""))) {
            getLogger().log(Level.FINEST,
                    "Refresh Request Handler only accepts requests from localhost addresses. Ignored request from "
                            + remoteAddr + " .");
            response.sendError(403, remoteAddr + " is not a localhost address");
            return true;
        }

        String requestToken = request.getParameter("refresh-token");
        if (token.equals(requestToken)) {
            /*
             * The client sent us the same token back - therefore we can
             * conclude that the handler has not been reloaded and the servlet
             * has not been reinitialized
             */
            try {
                /*
                 * sleep for an arbitrary amount of time - this is merely the
                 * amount of time we're willing to let this request stay open
                 */
                Thread.sleep(1000 * 60 * 5);
            } catch (InterruptedException e) {
                /*
                 * Sleep will be interrupted whenever the servlet is reloaded.
                 * This usually happens because we've changed a .java file,
                 * causing recompilation and redeployment.
                 */
            }
        }

        /*
         * When the time is up, the server shuts down (which interrupts sleep),
         * or we just received a refresh request with a different token (old or
         * null) we send a response.
         *
         * The client side should then send a new refresh request.
         */
        response.setContentType("text/plain");
        response.setHeader("X-VAADIN-REFRESH", token);

        try {
            session.lock();
            response.getWriter().write('|');
            for (UI ui : session.getUIs()) {
                response.getWriter().write(ui.getUIId());
                response.getWriter().write('|');
            }
        } finally {
            session.unlock();
        }
        return true;
    }

    private static Logger getLogger() {
        return Logger.getLogger(RefreshHandler.class.getName());
    }
}