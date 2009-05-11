package com.vaadin.demo.reservation.simple;

import java.security.Principal;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.model.User;
import com.liferay.portal.service.UserServiceUtil;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.PortletApplicationContext;
import com.vaadin.terminal.gwt.server.PortletApplicationContext.PortletListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * This is a stripped down version of Reservr example. Idea is to create simple,
 * but actually usable portal gadget. Example is Liferay spesific (in user
 * handling), so you need to add portal-kernel.jar and portal-service.jar files
 * to your classpath.
 * 
 */
public class SimpleReserver extends Application {

    private SampleDB db = new SampleDB();

    private StdView stdView = new StdView(this);

    private AdminView adminView = new AdminView(this);

    private Button toggleMode = new Button("Switch mode");

    private boolean isAdminView = false;

    private boolean isPortlet;

    protected User user;

    @Override
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

                    // save user object to application for later use
                    Principal userPrincipal = request.getUserPrincipal();
                    try {
                        user = UserServiceUtil.getUserById(Long
                                .parseLong(userPrincipal.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
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

    @Override
    public Object getUser() {
        if (getContext() instanceof PortletApplicationContext) {
            try {
                return user.getFirstName() + " " + user.getLastName();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "Portlet Demouser";
            }
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
