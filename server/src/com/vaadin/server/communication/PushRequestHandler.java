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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereFramework.AtmosphereHandlerWrapper;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.util.VoidAnnotationProcessor;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceDestroyEvent;
import com.vaadin.server.ServiceDestroyListener;
import com.vaadin.server.ServiceException;
import com.vaadin.server.ServletPortletHelper;
import com.vaadin.server.SessionExpiredHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletResponse;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushConstants;

/**
 * Handles requests to open a push (bidirectional) communication channel between
 * the client and the server. After the initial request, communication through
 * the push channel is managed by {@link PushAtmosphereHandler} and
 * {@link PushHandler}
 *
 * @author Vaadin Ltd
 * @since 7.1
 */
public class PushRequestHandler implements RequestHandler,
        SessionExpiredHandler {

    private AtmosphereFramework atmosphere;
    private PushHandler pushHandler;

    public PushRequestHandler(VaadinServletService service)
            throws ServiceException {

        service.addServiceDestroyListener(new ServiceDestroyListener() {
            @Override
            public void serviceDestroy(ServiceDestroyEvent event) {
                destroy();
            }
        });

        final ServletConfig vaadinServletConfig = service.getServlet()
                .getServletConfig();

        pushHandler = new PushHandler(service);

        atmosphere = getPreInitializedAtmosphere(vaadinServletConfig);
        if (atmosphere == null) {
            // Not initialized by JSR356WebsocketInitializer
            getLogger().fine(
                    "Initializing Atmosphere for servlet "
                            + vaadinServletConfig.getServletName());
            try {
                atmosphere = initAtmosphere(vaadinServletConfig);
            } catch (Exception e) {
                getLogger().log(
                        Level.WARNING,
                        "Failed to initialize Atmosphere for "
                                + service.getServlet().getServletName()
                                + ". Push will not work.", e);
                return;
            }
        } else {
            getLogger().fine(
                    "Using pre-initialized Atmosphere for servlet "
                            + vaadinServletConfig.getServletName());
        }

        for (AtmosphereHandlerWrapper handlerWrapper : atmosphere
                .getAtmosphereHandlers().values()) {
            AtmosphereHandler handler = handlerWrapper.atmosphereHandler;
            if (handler instanceof PushAtmosphereHandler) {
                // Map the (possibly pre-initialized) handler to the actual push
                // handler
                ((PushAtmosphereHandler) handler).setPushHandler(pushHandler);
            }

        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(PushRequestHandler.class.getName());
    }

    /**
     * Returns an AtmosphereFramework instance which was initialized in the
     * servlet context init phase by {@link JSR356WebsocketInitializer}, if such
     * exists
     */
    private AtmosphereFramework getPreInitializedAtmosphere(
            ServletConfig vaadinServletConfig) {
        String attributeName = JSR356WebsocketInitializer
                .getAttributeName(vaadinServletConfig.getServletName());
        Object framework = vaadinServletConfig.getServletContext()
                .getAttribute(attributeName);
        if (framework != null && framework instanceof AtmosphereFramework) {
            return (AtmosphereFramework) framework;
        }

        return null;
    }

    /**
     * Initializes Atmosphere for the given ServletConfiguration
     * 
     * @since 7.5.0
     * @param vaadinServletConfig
     *            The servlet configuration for the servlet which should have
     *            Atmosphere support
     */
    static AtmosphereFramework initAtmosphere(
            final ServletConfig vaadinServletConfig) {
        AtmosphereFramework atmosphere = new AtmosphereFramework(false, false) {
            @Override
            protected void analytics() {
                // Overridden to disable version number check
            }

            @Override
            public AtmosphereFramework addInitParameter(String name,
                    String value) {
                if (vaadinServletConfig.getInitParameter(name) == null) {
                    super.addInitParameter(name, value);
                }
                return this;
            }
        };

        atmosphere.addAtmosphereHandler("/*", new PushAtmosphereHandler());
        atmosphere.addInitParameter(ApplicationConfig.BROADCASTER_CACHE,
                UUIDBroadcasterCache.class.getName());
        atmosphere.addInitParameter(ApplicationConfig.ANNOTATION_PROCESSOR,
                VoidAnnotationProcessor.class.getName());
        atmosphere.addInitParameter(ApplicationConfig.PROPERTY_SESSION_SUPPORT,
                "true");
        atmosphere.addInitParameter(ApplicationConfig.MESSAGE_DELIMITER,
                String.valueOf(PushConstants.MESSAGE_DELIMITER));
        atmosphere.addInitParameter(
                ApplicationConfig.DROP_ACCESS_CONTROL_ALLOW_ORIGIN_HEADER,
                "false");
        // Disable heartbeat (it does not emit correct events client side)
        // https://github.com/Atmosphere/atmosphere-javascript/issues/141
        atmosphere.addInitParameter(
                ApplicationConfig.DISABLE_ATMOSPHEREINTERCEPTORS,
                HeartbeatInterceptor.class.getName());

        final String bufferSize = String
                .valueOf(PushConstants.WEBSOCKET_BUFFER_SIZE);
        atmosphere.addInitParameter(ApplicationConfig.WEBSOCKET_BUFFER_SIZE,
                bufferSize);
        atmosphere.addInitParameter(ApplicationConfig.WEBSOCKET_MAXTEXTSIZE,
                bufferSize);
        atmosphere.addInitParameter(ApplicationConfig.WEBSOCKET_MAXBINARYSIZE,
                bufferSize);
        atmosphere.addInitParameter(
                ApplicationConfig.PROPERTY_ALLOW_SESSION_TIMEOUT_REMOVAL,
                "false");
        // Disable Atmosphere's message about commercial support
        atmosphere.addInitParameter("org.atmosphere.cpr.showSupportMessage",
                "false");

        try {
            atmosphere.init(vaadinServletConfig);

            // Ensure the client-side knows how to split the message stream
            // into individual messages when using certain transports
            AtmosphereInterceptor trackMessageSize = new TrackMessageSizeInterceptor();
            trackMessageSize.configure(atmosphere.getAtmosphereConfig());
            atmosphere.interceptor(trackMessageSize);
        } catch (ServletException e) {
            throw new RuntimeException("Atmosphere init failed", e);
        }
        return atmosphere;
    }

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {

        if (!ServletPortletHelper.isPushRequest(request)) {
            return false;
        }

        if (request instanceof VaadinServletRequest) {
            if (atmosphere == null) {
                response.sendError(500,
                        "Atmosphere initialization failed. No push available.");
                return true;
            }
            try {
                atmosphere.doCometSupport(AtmosphereRequest
                        .wrap((VaadinServletRequest) request),
                        AtmosphereResponse
                                .wrap((VaadinServletResponse) response));
            } catch (ServletException e) {
                // TODO PUSH decide how to handle
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException(
                    "Portlets not currently supported");
        }

        return true;
    }

    public void destroy() {
        atmosphere.destroy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.server.SessionExpiredHandler#handleSessionExpired(com.vaadin
     * .server.VaadinRequest, com.vaadin.server.VaadinResponse)
     */
    @Override
    public boolean handleSessionExpired(VaadinRequest request,
            VaadinResponse response) throws IOException {
        // Websockets request must be handled by accepting the websocket
        // connection and then sending session expired so we let
        // PushRequestHandler handle it
        return handleRequest(null, request, response);
    }
}
