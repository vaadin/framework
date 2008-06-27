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

public class ApplicationPortlet implements Portlet {
    // The application to show
    protected String app = null;
    // some applications might require forced height (and, more seldom, width)
    protected String style = null; // e.g "height:500px;"
    protected String widgetset = null;

    public void destroy() {

    }

    public void init(PortletConfig config) throws PortletException {
        app = config.getInitParameter("application");
        if (app == null) {
            app = "PortletDemo";
        }
        style = config.getInitParameter("style");
        widgetset = config.getInitParameter("widgetset");
    }

    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException {
        PortletApplicationContext.dispatchRequest(this, request, response);
    }

    public void render(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        // display the IT Mill Toolkit application
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
                request.setAttribute(ApplicationServlet.REQUEST_FRAGMENT,
                        "true");
                if (widgetset != null) {
                    request.setAttribute(ApplicationServlet.REQUEST_WIDGETSET,
                            widgetset);
                }
                if (style != null) {
                    request.setAttribute(ApplicationServlet.REQUEST_APPSTYLE,
                            style);
                }
                dispatcher.include(request, response);

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

}
