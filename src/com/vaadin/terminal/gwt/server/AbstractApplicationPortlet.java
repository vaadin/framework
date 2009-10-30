package com.vaadin.terminal.gwt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;
import javax.portlet.ResourceURL;

import com.vaadin.Application;
import com.vaadin.external.org.apache.commons.fileupload.portlet.PortletFileUpload;
import com.vaadin.ui.Window;

public abstract class AbstractApplicationPortlet implements Portlet,
        ResourceServingPortlet {

    public void destroy() {
        // TODO Auto-generated method stub
    }

    public void init(PortletConfig config) throws PortletException {
        // TODO Auto-generated method stub
    }

    enum RequestType {
        FILE_UPLOAD, UIDL, RENDER, STATIC_FILE, UNKNOWN;
    }

    protected RequestType getRequestType(PortletRequest request) {
        if (request instanceof RenderRequest) {
            return RequestType.RENDER;
        } else if (request instanceof ResourceRequest) {
            if (isStaticResourceRequest((ResourceRequest) request)) {
                return RequestType.STATIC_FILE;
            }
        } else if (request instanceof ActionRequest) {
            if (isUIDLRequest((ActionRequest) request)) {
                return RequestType.UIDL;
            } else if (isFileUploadRequest((ActionRequest) request)) {
                return RequestType.FILE_UPLOAD;
            }
        }
        return RequestType.UNKNOWN;
    }

    private boolean isStaticResourceRequest(ResourceRequest request) {
        String resourceID = request.getResourceID();
        if (resourceID != null && resourceID.startsWith("/VAADIN/")) {
            return true;
        }
        return false;
    }

    private boolean isUIDLRequest(ActionRequest request) {
        return request.getParameter("UIDL") != null;
    }

    private boolean isFileUploadRequest(ActionRequest request) {
        return PortletFileUpload.isMultipartContent(request);
    }

    protected void handleRequest(PortletRequest request,
            PortletResponse response) throws PortletException, IOException {
        System.out.println("AbstractApplicationPortlet.handleRequest()");

        RequestType requestType = getRequestType(request);

        if (requestType == RequestType.UNKNOWN) {
            System.out.println("Unknown request type");
            return;
        } else if (requestType == RequestType.STATIC_FILE) {
            serveStaticResources((ResourceRequest) request,
                    (ResourceResponse) response);
            return;
        }

        Application application = null;

        try {
            /* Find out which application this request is related to */
            application = findApplicationInstance(request);
            if (application == null) {
                return;
            }

            /*
             * Get or create an application context and an application manager
             * for the session
             */
            PortletApplicationContext2 applicationContext = PortletApplicationContext2
                    .getApplicationContext(request.getPortletSession());
            PortletCommunicationManager applicationManager = applicationContext
                    .getApplicationManager(application);

            /* Update browser information from request */
            applicationContext.getBrowser().updateBrowserProperties(request);

            /* Start the newly created application */
            startApplication(request, application, applicationManager);

            /*
             * Transaction starts. Call transaction listeners. Transaction end
             * is called in the finally block below.
             */
            applicationContext.startTransaction(application, request);

            /* Handle the request */
            if (requestType == RequestType.FILE_UPLOAD) {
                applicationManager.handleFileUpload((ActionRequest) request, (ActionResponse) response);
                return;
            } else if (requestType == RequestType.UIDL) {
                // Handles AJAX UIDL requests
                applicationManager.handleUIDLRequest((ActionRequest) request, (ActionResponse) response);
                return;
            } else if (requestType == RequestType.RENDER) {
                /*
                 * Removes the application if it has stopped
                 */
                if (!application.isRunning()) {
                    endApplication(request, response, application);
                    return;
                }

                /*
                 * Finds the window within the application
                 */
                Window window = getApplicationWindow(request,
                        applicationManager, application);
                if (window == null) {
                    throw new PortletException(Constants.ERROR_NO_WINDOW_FOUND);
                }

                /*
                 * Sets terminal type for the window, if not already set
                 */
                if (window.getTerminal() == null) {
                    window.setTerminal(applicationContext.getBrowser());
                }

                /*
                 * Handle parameters
                 */
                final Map<String, String[]> parameters = request
                        .getParameterMap();
                if (window != null && parameters != null) {
                    window.handleParameters(parameters);
                }

                /*
                 * Send initial AJAX page that kickstarts the Vaadin application
                 */
                writeAjaxPage((RenderRequest) request,
                        (RenderResponse) response, window, application);
            }
        } catch (final Throwable e) {
            // TODO Handle exceptions
        } finally {
            // Notifies transaction end
            if (application != null) {
                ((PortletApplicationContext2) application.getContext())
                        .endTransaction(application, request);
            }
        }
    }

    private void serveStaticResources(ResourceRequest request,
            ResourceResponse response) {
        // TODO Auto-generated method stub

    }

    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException {
        System.out.println("AbstractApplicationPortlet.processAction()");
        handleRequest(request, response);
    }

    public void render(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        System.out.println("AbstractApplicationPortlet.render()");
        handleRequest(request, response);
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response)
            throws PortletException, IOException {
        System.out.println("AbstractApplicationPortlet.serveResource()");
        handleRequest(request, response);
    }

    private Window getApplicationWindow(PortletRequest request,
            PortletCommunicationManager applicationManager,
            Application application) throws PortletException {
        // TODO Auto-generated method stub
        return null;
    }

    private void startApplication(PortletRequest request,
            Application application,
            PortletCommunicationManager applicationManager)
            throws PortletException, MalformedURLException {
        // TODO Auto-generated method stub
    }

    private void endApplication(PortletRequest request,
            PortletResponse response, Application application)
            throws IOException {
        // TODO Auto-generated method stub
    }

    private Application findApplicationInstance(PortletRequest request)
            throws PortletException, SessionExpired, MalformedURLException {
        return null;
    }

    protected void writeAjaxPage(RenderRequest request,
            RenderResponse response, Window window, Application application)
            throws IOException, MalformedURLException, PortletException {
        final BufferedWriter page = new BufferedWriter(new OutputStreamWriter(
                response.getPortletOutputStream(), "UTF-8"));

        response.setContentType("text/html");

        // TODO Figure out the format of resource URLs
        ResourceURL widgetsetURL = response.createResourceURL();
        // TODO Add support for custom widgetsets.
        widgetsetURL.setResourceID(Constants.DEFAULT_WIDGETSET);

        page.write("<script type=\"text/javascript\">\n");
        page.write("if(!vaadin || !vaadin.vaadinConfigurations) {\n "
                + "if(!vaadin) { var vaadin = {}} \n"
                + "vaadin.vaadinConfigurations = {};\n"
                + "if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }\n");
        // TODO Add support for production mode
        page.write("vaadin.debug = true;\n");
        page
                .write("document.write('<iframe tabIndex=\"-1\" id=\"__gwt_historyFrame\" "
                        + "style=\"width:0;height:0;border:0;overflow:"
                        + "hidden\" src=\"javascript:false\"></iframe>');\n");
        page.write("document.write(\"<script language='javascript' src='"
                + widgetsetURL.toString() + "'><\\/script>\");\n}\n");

        page.write("vaadin.vaadinConfigurations[\"" + request.getWindowID() + "\"] = {");
        page.write("portletMode: true, ");
        
        String pathInfo = response.createActionURL().toString();

        page.write("pathInfo: '" + pathInfo + "', ");
        // TODO Custom window
        if (window != application.getMainWindow()) {
            page.write("windowName: '" + window.getName() + "', ");
        }
        page.write("themeUri:");
        // page.write(themeUri != null ? "'" + themeUri + "'" : "null");
        page.write("null"); // TODO Fix this
        page.write(", versionInfo : {vaadinVersion:\"");
        // page.write(VERSION);
        page.write("UNVERSIONED"); // TODO Fix this
        page.write("\",applicationVersion:\"");
        page.write(application.getVersion());
        page.write("\"},");
        // TODO Add system messages
        page.write("};\n</script>\n");
        // TODO Add custom theme
        // TODO Warn if widgetset has not been loaded after 15 seconds
        page.close();
    }

    protected abstract Class<? extends Application> getApplicationClass()
            throws ClassNotFoundException;

    protected Application getNewApplication(PortletRequest request)
            throws PortletException {
        try {
            final Application application = getApplicationClass().newInstance();
            return application;
        } catch (final IllegalAccessException e) {
            throw new PortletException("getNewApplication failed", e);
        } catch (final InstantiationException e) {
            throw new PortletException("getNewApplication failed", e);
        } catch (final ClassNotFoundException e) {
            throw new PortletException("getNewApplication failed", e);
        }
    }

    protected ClassLoader getClassLoader() throws PortletException {
        // TODO Add support for custom class loader
        return getClass().getClassLoader();
    }
}
