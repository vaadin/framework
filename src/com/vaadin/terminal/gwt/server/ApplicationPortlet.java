package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortalContext;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.util.PropsUtil;
import com.vaadin.Application;

@SuppressWarnings("serial")
public class ApplicationPortlet implements Portlet, Serializable {
    // portlet configuration parameters
    private static final String PORTLET_PARAMETER_APPLICATION = "application";
    private static final String PORTLET_PARAMETER_STYLE = "style";
    private static final String PORTLET_PARAMETER_WIDGETSET = "widgetset";

    // portal configuration parameters
    private static final String PORTAL_PARAMETER_VAADIN_WIDGETSET = "vaadin.widgetset";
    private static final String PORTAL_PARAMETER_VAADIN_RESOURCE_PATH = "vaadin.resources.path";
    private static final String PORTAL_PARAMETER_VAADIN_THEME = "vaadin.theme";

    // The application to show
    protected String app = null;
    // some applications might require forced height (and, more seldom, width)
    protected String style = null; // e.g "height:500px;"
    // force the portlet to use this widgetset - portlet level setting
    protected String portletWidgetset = null;

    public void destroy() {

    }

    public void init(PortletConfig config) throws PortletException {
        app = config.getInitParameter(PORTLET_PARAMETER_APPLICATION);
        if (app == null) {
            throw new PortletException(
                    "No porlet application url defined in portlet.xml. Define the '"
                            + PORTLET_PARAMETER_APPLICATION
                            + "' init parameter to be the servlet deployment path.");
        }
        style = config.getInitParameter(PORTLET_PARAMETER_STYLE);
        // enable forcing the selection of the widgetset in portlet
        // configuration for a single portlet (backwards compatibility)
        portletWidgetset = config.getInitParameter(PORTLET_PARAMETER_WIDGETSET);
    }

    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException {
        PortletApplicationContext.dispatchRequest(this, request, response);
    }

    public void render(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        // display the Vaadin application
        writeAjaxWindow(request, response);
    }

    protected void writeAjaxWindow(RenderRequest request,
            RenderResponse response) throws IOException {

        response.setContentType("text/html");
        if (app != null) {
            PortletSession sess = request.getPortletSession();
            PortletApplicationContext ctx = PortletApplicationContext
                    .getApplicationContext(sess);

            PortletRequestDispatcher dispatcher = sess.getPortletContext()
                    .getRequestDispatcher("/" + app);

            try {
                // portal-wide settings
                PortalContext portalCtx = request.getPortalContext();

                boolean isLifeRay = portalCtx.getPortalInfo().toLowerCase()
                        .contains("liferay");

                request.setAttribute(ApplicationServlet.REQUEST_FRAGMENT,
                        "true");

                // fixed base theme to use - all portal pages with Vaadin
                // applications will load this exactly once
                String portalTheme = getPortalProperty(
                        PORTAL_PARAMETER_VAADIN_THEME, portalCtx);

                String portalWidgetset = getPortalProperty(
                        PORTAL_PARAMETER_VAADIN_WIDGETSET, portalCtx);

                // location of the widgetset(s) and default theme (to which
                // /VAADIN/widgetsets/...
                // is appended)
                String portalResourcePath = getPortalProperty(
                        PORTAL_PARAMETER_VAADIN_RESOURCE_PATH, portalCtx);

                // by default on LifeRay, widgetset and default theme is in
                // /html/VAADIN/widgetsets/...
                if (isLifeRay && portalResourcePath == null) {
                    portalResourcePath = "/html";
                }

                // - if the user has specified a widgetset for this portlet, use
                // it from the portlet (not fully supported)
                // - otherwise, if specified, use the portal-wide widgetset
                // and widgetset path settings (recommended)
                // - finally, default to use the default widgetset if nothing
                // else is found
                if (portalWidgetset != null) {
                    if (portalResourcePath != null) {
                        request
                                .setAttribute(
                                        ApplicationServlet.REQUEST_VAADIN_WIDGETSET_PATH,
                                        portalResourcePath);
                    }

                    request.setAttribute(ApplicationServlet.REQUEST_WIDGETSET,
                            portalWidgetset);
                } else if (portletWidgetset != null) {
                    request.setAttribute(ApplicationServlet.REQUEST_WIDGETSET,
                            portletWidgetset);
                }

                if (style != null) {
                    request.setAttribute(ApplicationServlet.REQUEST_APPSTYLE,
                            style);
                }

                String themeUri = null;
                if (portalTheme != null) {
                    themeUri = portalResourcePath + "/"
                            + AbstractApplicationServlet.THEME_DIRECTORY_PATH
                            + portalTheme;
                    request.setAttribute(
                            ApplicationServlet.REQUEST_DEFAULT_THEME_URI,
                            portalTheme);
                }

                dispatcher.include(request, response);

                if (isLifeRay) {
                    /*
                     * Temporary support to heartbeat Liferay session when using
                     * Vaadin based portlet. We hit an extra xhr to liferay
                     * servlet to extend the session lifetime after each Vaadin
                     * request. This hack can be removed when supporting portlet
                     * 2.0 and resourceRequests.
                     * 
                     * TODO make this configurable, this is not necessary with
                     * some custom session configurations.
                     */
                    OutputStream out = response.getPortletOutputStream();

                    String lifeRaySessionHearbeatHack = ("<script type=\"text/javascript\">"
                            + "if(!vaadin.postRequestHooks) {"
                            + "    vaadin.postRequestHooks = {};"
                            + "}"
                            + "vaadin.postRequestHooks.liferaySessionHeartBeat = function() {"
                            + "    if (Liferay && Liferay.Session) {"
                            + "        Liferay.Session.extend();"
                            + "    }"
                            + "};" + "</script>");
                    out.write(lifeRaySessionHearbeatHack.getBytes());
                }

                /*
                 * Make sure portal default Vaadin theme is included exactly
                 * once in DOM.
                 */
                if (portalTheme != null) {
                    OutputStream out = response.getPortletOutputStream();

                    // Using portal-wide theme
                    String loadDefaultTheme = ("<script type=\"text/javascript\">\n"
                            + "if(!vaadin.themesLoaded['"
                            + portalTheme
                            + "']) {\n"
                            + "var stylesheet = document.createElement('link');\n"
                            + "stylesheet.setAttribute('rel', 'stylesheet');\n"
                            + "stylesheet.setAttribute('type', 'text/css');\n"
                            + "stylesheet.setAttribute('href', '"
                            + themeUri
                            + "/styles.css');\n"
                            + "document.getElementsByTagName('head')[0].appendChild(stylesheet);\n"
                            + "vaadin.themesLoaded['"
                            + portalTheme
                            + "'] = true;\n}\n" + "</script>\n");
                    out.write(loadDefaultTheme.getBytes());
                }

            } catch (PortletException e) {
                PrintWriter out = response.getWriter();
                out.print("<h1>Servlet include failed!</h1>");
                out.print("<div>" + e + "</div>");
                ctx.setPortletApplication(this, null);
                return;
            }

            Application app = (Application) request
                    .getAttribute(Application.class.getName());
            ctx.setPortletApplication(this, app);
            ctx.firePortletRenderRequest(this, request, response);

        }
    }

    private String getPortalProperty(String name, PortalContext context) {
        boolean isLifeRay = context.getPortalInfo().toLowerCase().contains(
                "liferay");

        // TODO test on non-LifeRay platforms

        String value;
        if (isLifeRay) {
            value = getLifeRayPortalProperty(name);
        } else {
            value = context.getProperty(name);
        }

        return value;
    }

    private String getLifeRayPortalProperty(String name) {
        String value;
        try {
            value = PropsUtil.get(name);
        } catch (Exception e) {
            value = null;
        }
        return value;
    }
}
