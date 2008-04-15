package com.itmill.toolkit.terminal.gwt.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.itmill.toolkit.Application;

public class ITMillApplicationPortlet implements Portlet {
    // The application to show
    protected String app = "Calc";
    // some applications might require that the height is specified
    protected String height = null; // e.g "200px"

    private PortletConfig config;

    public void destroy() {
        config = null;
    }

    public void init(PortletConfig config) throws PortletException {
        this.config = config;
        app = config.getInitParameter("application");
        if (app == null) {
            app = "PortletDemo";
        }
        height = config.getInitParameter("height");
    }

    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException {
        PortletApplicationContext.dispatchRequest(this, request, response);
    }

    public void render(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        /*-
        PortletPreferences prefs = request.getPreferences();
        app = prefs.getValue("application", app);
        theme = prefs.getValue("theme", "default");
        height = prefs.getValue("height", null);
        -*/

        // display the IT Mill Toolkit application
        writeAjaxWindow(request, response);
    }

    protected void writeAjaxWindow(RenderRequest request,
            RenderResponse response) throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (app != null) {
            PortletSession sess = request.getPortletSession();
            PortletApplicationContext ctx = PortletApplicationContext
                    .getApplicationContext(sess);

            /*- TODO store som info somewhere?
            PortletInfo pi = ctx.setPortletInfo(request.getContextPath() + "/"
                    + app + (app.endsWith("/") ? "" : "/"), request
                    .getPortletMode(), request.getWindowState(), request
                    .getUserPrincipal(), (Map) request
                    .getAttribute(PortletRequest.USER_INFO), config);
            -*/

            PortletRequestDispatcher dispatcher = sess.getPortletContext()
                    .getRequestDispatcher("/" + app);

            try {
                // TODO remove:

                System.err.println(request.getContextPath() + " (portlet ctx)");
                // TODO height
                dispatcher.include(request, response);

            } catch (PortletException e) {
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

}
