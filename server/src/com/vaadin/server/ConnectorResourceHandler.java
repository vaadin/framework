package com.vaadin.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.UI;

public class ConnectorResourceHandler implements RequestHandler {
    // APP/connector/[uiid]/[cid]/[filename.xyz]
    private static final Pattern CONNECTOR_RESOURCE_PATTERN = Pattern
            .compile("^/?" + ApplicationConstants.APP_REQUEST_PATH
                    + ConnectorResource.CONNECTOR_REQUEST_PATH
                    + "(\\d+)/(\\d+)/(.*)");

    private static Logger getLogger() {
        return Logger.getLogger(ConnectorResourceHandler.class.getName());

    }

    @Override
    public boolean handleRequest(Application application,
            WrappedRequest request, WrappedResponse response)
            throws IOException {
        String requestPath = request.getRequestPathInfo();
        if (requestPath == null) {
            return false;
        }
        Matcher matcher = CONNECTOR_RESOURCE_PATTERN.matcher(requestPath);
        if (matcher.matches()) {
            String uiId = matcher.group(1);
            String cid = matcher.group(2);
            String key = matcher.group(3);
            UI ui = application.getUIById(Integer.parseInt(uiId));
            if (ui == null) {
                return error(request, response,
                        "Ignoring connector request for no-existent root "
                                + uiId);
            }

            UI.setCurrent(ui);
            Application.setCurrent(ui.getApplication());

            ClientConnector connector = ui.getConnectorTracker().getConnector(
                    cid);
            if (connector == null) {
                return error(request, response,
                        "Ignoring connector request for no-existent connector "
                                + cid + " in root " + uiId);
            }

            if (!connector.handleConnectorRequest(request, response, key)) {
                return error(request, response, connector.getClass()
                        .getSimpleName()
                        + " ("
                        + connector.getConnectorId()
                        + ") did not handle connector request for " + key);
            }

            return true;
        } else {
            return false;
        }
    }

    private static boolean error(WrappedRequest request,
            WrappedResponse response, String logMessage) throws IOException {
        getLogger().log(Level.WARNING, logMessage);
        response.sendError(HttpServletResponse.SC_NOT_FOUND,
                request.getRequestPathInfo() + " can not be found");

        // Request handled (though not in a nice way)
        return true;
    }
}
