/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;

import com.vaadin.Application;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.VariableOwner;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.ui.Component;
import com.vaadin.ui.Root;

/**
 * Application manager processes changes and paints for single application
 * instance.
 * 
 * This class handles applications running as servlets.
 * 
 * @see AbstractCommunicationManager
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
@SuppressWarnings("serial")
public class CommunicationManager extends AbstractCommunicationManager {

    /**
     * @deprecated use {@link #CommunicationManager(Application)} instead
     * @param application
     * @param applicationServlet
     */
    @Deprecated
    public CommunicationManager(Application application,
            AbstractApplicationServlet applicationServlet) {
        super(application);
    }

    /**
     * TODO New constructor - document me!
     * 
     * @param application
     */
    public CommunicationManager(Application application) {
        super(application);
    }

    /**
     * Handles file upload request submitted via Upload component.
     * 
     * @see #getStreamVariableTargetUrl(ReceiverOwner, String, StreamVariable)
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws InvalidUIDLSecurityKeyException
     */
    public void handleFileUpload(WrappedRequest request,
            WrappedResponse response) throws IOException,
            InvalidUIDLSecurityKeyException {

        /*
         * URI pattern: APP/UPLOAD/[PID]/[NAME]/[SECKEY] See #createReceiverUrl
         */

        String pathInfo = request.getRequestPathInfo();
        // strip away part until the data we are interested starts
        int startOfData = pathInfo
                .indexOf(AbstractApplicationServlet.UPLOAD_URL_PREFIX)
                + AbstractApplicationServlet.UPLOAD_URL_PREFIX.length();
        String uppUri = pathInfo.substring(startOfData);
        String[] parts = uppUri.split("/", 3); // 0 = pid, 1= name, 2 = sec key
        String variableName = parts[1];
        String paintableId = parts[0];

        StreamVariable streamVariable = pidToNameToStreamVariable.get(
                paintableId).get(variableName);
        String secKey = streamVariableToSeckey.get(streamVariable);
        if (secKey.equals(parts[2])) {

            VariableOwner source = getVariableOwner(paintableId);
            String contentType = request.getContentType();
            if (contentType.contains("boundary")) {
                // Multipart requests contain boundary string
                doHandleSimpleMultipartFileUpload(request, response,
                        streamVariable, variableName, source,
                        contentType.split("boundary=")[1]);
            } else {
                // if boundary string does not exist, the posted file is from
                // XHR2.post(File)
                doHandleXhrFilePost(request, response, streamVariable,
                        variableName, source, request.getContentLength());
            }
        } else {
            throw new InvalidUIDLSecurityKeyException(
                    "Security key in upload post did not match!");
        }

    }

    @Override
    protected void unregisterPaintable(Component p) {
        /* Cleanup possible receivers */
        if (pidToNameToStreamVariable != null) {
            Map<String, StreamVariable> removed = pidToNameToStreamVariable
                    .remove(getPaintableId(p));
            if (removed != null) {
                for (String key : removed.keySet()) {
                    streamVariableToSeckey.remove(removed.get(key));
                }
            }
        }
        super.unregisterPaintable(p);

    }

    private Map<String, Map<String, StreamVariable>> pidToNameToStreamVariable;

    private Map<StreamVariable, String> streamVariableToSeckey;

    @Override
    String getStreamVariableTargetUrl(VariableOwner owner, String name,
            StreamVariable value) {
        /*
         * We will use the same APP/* URI space as ApplicationResources but
         * prefix url with UPLOAD
         * 
         * eg. APP/UPLOAD/[PID]/[NAME]/[SECKEY]
         * 
         * SECKEY is created on each paint to make URL's unpredictable (to
         * prevent CSRF attacks).
         * 
         * NAME and PID from URI forms a key to fetch StreamVariable when
         * handling post
         */
        String paintableId = getPaintableId((Paintable) owner);
        String key = paintableId + "/" + name;

        if (pidToNameToStreamVariable == null) {
            pidToNameToStreamVariable = new HashMap<String, Map<String, StreamVariable>>();
        }
        Map<String, StreamVariable> nameToStreamVariable = pidToNameToStreamVariable
                .get(paintableId);
        if (nameToStreamVariable == null) {
            nameToStreamVariable = new HashMap<String, StreamVariable>();
            pidToNameToStreamVariable.put(paintableId, nameToStreamVariable);
        }
        nameToStreamVariable.put(name, value);

        if (streamVariableToSeckey == null) {
            streamVariableToSeckey = new HashMap<StreamVariable, String>();
        }
        String seckey = streamVariableToSeckey.get(value);
        if (seckey == null) {
            seckey = UUID.randomUUID().toString();
            streamVariableToSeckey.put(value, seckey);
        }

        return "app://" + AbstractApplicationServlet.UPLOAD_URL_PREFIX + key
                + "/" + seckey;

    }

    @Override
    protected void cleanStreamVariable(VariableOwner owner, String name) {
        Map<String, StreamVariable> nameToStreamVar = pidToNameToStreamVariable
                .get(getPaintableId((Paintable) owner));
        nameToStreamVar.remove("name");
        if (nameToStreamVar.isEmpty()) {
            pidToNameToStreamVariable.remove(getPaintableId((Paintable) owner));
        }
    }

    @Override
    protected AjaxPageHandler createAjaxPageHandler() {
        return new AjaxPageHandler() {
            @Override
            protected String getApplicationId(AjaxPageContext context) {
                String appUrl = getAppUri(context);

                String appId = appUrl;
                if ("".equals(appUrl)) {
                    appId = "ROOT";
                }
                appId = appId.replaceAll("[^a-zA-Z0-9]", "");
                // Add hashCode to the end, so that it is still (sort of)
                // predictable, but indicates that it should not be used in CSS
                // and
                // such:
                int hashCode = appId.hashCode();
                if (hashCode < 0) {
                    hashCode = -hashCode;
                }
                appId = appId + "-" + hashCode;
                return appId;
            }

            @Override
            protected String getAppUri(AjaxPageContext context) {
                /* Fetch relative url to application */
                // don't use server and port in uri. It may cause problems with
                // some
                // virtual server configurations which lose the server name
                Application application = context.getApplication();
                URL url = application.getURL();
                String appUrl = url.getPath();
                if (appUrl.endsWith("/")) {
                    appUrl = appUrl.substring(0, appUrl.length() - 1);
                }
                return appUrl;
            }

            @Override
            public String getThemeName(AjaxPageContext context) {
                String themeName = context.getRequest().getParameter(
                        AbstractApplicationServlet.URL_PARAMETER_THEME);
                if (themeName == null) {
                    themeName = super.getThemeName(context);
                }
                return themeName;
            }

            @Override
            protected String getInitialUIDL(WrappedRequest request, Root root)
                    throws PaintException {
                return CommunicationManager.this.getInitialUIDL(request, root);
            }
        };
    }

    @Override
    protected InputStream getThemeResourceAsStream(Root root, String themeName,
            String resource) {
        WebApplicationContext context = (WebApplicationContext) root
                .getApplication().getContext();
        ServletContext servletContext = context.getHttpSession()
                .getServletContext();
        return servletContext.getResourceAsStream("/"
                + AbstractApplicationServlet.THEME_DIRECTORY_PATH + themeName
                + "/" + resource);
    }
}
