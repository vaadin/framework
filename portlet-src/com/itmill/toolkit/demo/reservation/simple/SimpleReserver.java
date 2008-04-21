package com.itmill.toolkit.demo.reservation.simple;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.gwt.server.PortletApplicationContext;
import com.itmill.toolkit.terminal.gwt.server.PortletApplicationContext.PortletListener;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

/**
 * This is a stripped down version of Reservr example. Idea is to create simple,
 * but actually usable portal gadget.
 * 
 */
public class SimpleReserver extends Application {

    private SampleDB db = new SampleDB();

    private StdView stdView = new StdView(this);

    private AdminView adminView = new AdminView(this);

    private Button toggleMode = new Button("Switch mode");

    private boolean isAdminView = false;

    private boolean isPortlet;

    public void init() {
        final Window w = new Window("Simple Reserver");
        w.addStyleName("simplereserver");

        if (getContext() instanceof PortletApplicationContext) {
            isPortlet = true;
            PortletApplicationContext context = (PortletApplicationContext) getContext();
            context.addPortletListener(this, new PortletListener() {
                public void handleActionRequest(ActionRequest request,
                        ActionResponse response) {

                }

                public void handleRenderRequest(RenderRequest request,
                        RenderResponse response) {
                    // react on mode changes
                    if ((request.getPortletMode() == PortletMode.EDIT && !isAdminView)
                            || (request.getPortletMode() == PortletMode.VIEW && isAdminView)) {
                        toggleMode();
                    }

                }
            });
            w.setTheme("liferay");
            // portal will deal outer margins
            w.getLayout().setMargin(false);
        } else {
            w.setTheme("reservr");
        }

        setMainWindow(w);
        if (!isPortlet) {
            // only use toggle mode button when not in portal
            w.addComponent(toggleMode);
            toggleMode.addListener(new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    toggleMode();
                }
            });
        }
        w.addComponent(stdView);
    }

    protected void toggleMode() {
        OrderedLayout main = (OrderedLayout) getMainWindow().getLayout();
        isAdminView = !isAdminView;
        if (isAdminView) {
            main.replaceComponent(stdView, adminView);
        } else {
            main.replaceComponent(adminView, stdView);
            stdView.refreshData();
        }
    }

    public SampleDB getDb() {
        return db;
    }

    public Object getUser() {
        if (getContext() instanceof PortletApplicationContext) {
            PortletApplicationContext context = (PortletApplicationContext) getContext();
            Object username = context.getPortletSession().getAttribute(
                    "userName", PortletSession.APPLICATION_SCOPE);
            if (username == null) {
                return "Guest Portaluser";
            }
            return username.toString();

        } else {
            Object user = super.getUser();
            if (user == null) {
                return "Demo User";
            } else {
                return user;
            }
        }

    }

}
