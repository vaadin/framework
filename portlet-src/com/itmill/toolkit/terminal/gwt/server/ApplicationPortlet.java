package com.itmill.toolkit.terminal.gwt.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.gwt.server.PortletApplicationContext.PortletInfo;
import com.itmill.toolkit.terminal.gwt.server.PortletApplicationContext.PortletInfoReceiver;

public class ApplicationPortlet implements Portlet {
    // The application to show
    protected String app = "Calc"; // empty for root
    // theme to use for the application
    protected String theme = "default";
    // some applications might require that the height is specified
    protected String height = null; // e.g "200px"

    PortletConfig config;

    public void destroy() {
        config = null;
    }

    public void init(PortletConfig config) throws PortletException {
        this.config = config;
    }

    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException {
        // Update preferences (configured)
        PortletPreferences prefs = request.getPreferences();
        app = request.getParameter("app");
        if (app != null && app.length() > 0) {
            prefs.setValue("application", app);
        } else {
            app = null;
        }
        String theme = request.getParameter("theme");
        if (theme != null && theme.length() > 0) {
            prefs.setValue("theme", theme);
        } else {
            prefs.setValue("theme", null);
        }
        String height = request.getParameter("height");
        if (height != null && height.length() > 0) {
            prefs.setValue("height", height);
        } else {
            prefs.setValue("height", null);
        }
        prefs.store();
    }

    public void render(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        PortletPreferences prefs = request.getPreferences();
        app = prefs.getValue("application", app);
        theme = prefs.getValue("theme", "default");
        height = prefs.getValue("height", null);

        // display the IT Mill Toolkit application
        writeAjaxWindow(request, response);
    }

    protected void writeAjaxWindow(RenderRequest request,
            RenderResponse response) throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // TODO check user == admin
        if (app == null) {
            // Display the configuration UI
            PortletURL submitUrl = response.createActionURL();
            try {
                submitUrl.setPortletMode(PortletMode.VIEW);
            } catch (PortletModeException e) {
                // Fine
            }
            out.println("<form method='POST' action='" + submitUrl + "'>");
            out.println("Application:");
            out.println(request.getContextPath() + "/");
            out.println("<input size='40' type='text' name='app' value='"
                    + (app != null ? app : "") + "'>");
            out.println(" Theme:<input type='text' name='theme' value='"
                    + theme + "'><br/>");
            out
                    .println("Force height (optional, e.g \"200px\"): <input type='text' name='height' value='"
                            + (height != null ? height : "") + "'><br/>");
            out.println("<input type='submit' value='Save'>");
            out.println("</form>");
        } else {

            PortletSession sess = request.getPortletSession();
            PortletApplicationContext ctx = PortletApplicationContext
                    .getApplicationContext(sess);

            PortletInfo pi = ctx.setPortletInfo(request.getContextPath() + "/"
                    + app + (app.endsWith("/") ? "" : "/"), request
                    .getPortletMode(), request.getWindowState(), request
                    .getUserPrincipal(), (Map) request
                    .getAttribute(PortletRequest.USER_INFO), config);

            PortletRequestDispatcher dispatcher = sess.getPortletContext()
                    .getRequestDispatcher("/" + app);

            try {
                dispatcher.include(request, response);
            } catch (PortletException e) {
                out.print("<h1>Servlet include failed!</h1>");
                out.print("<div>" + e + "</div>");
                return;
            }

            Object app = request.getAttribute(Application.class.getName());
            if (app instanceof PortletInfoReceiver) {
                ((PortletInfoReceiver) app).receivePortletInfo(pi);
            }
        }
    }

}
