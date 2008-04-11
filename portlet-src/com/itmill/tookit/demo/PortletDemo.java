/**
 * 
 */
package com.itmill.tookit.demo;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.gwt.server.PortletApplicationContext;
import com.itmill.toolkit.terminal.gwt.server.PortletApplicationContext.PortletListener;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Window.Notification;

/**
 * @author marc
 * 
 */
public class PortletDemo extends Application {

    Window main = new Window();
    TextField tf = new TextField();
    Link portletEdit = new Link();
    Link portletMax = new Link();

    public void init() {
        main = new Window();
        tf.setEnabled(false);
        main.addComponent(tf);

        portletEdit.setCaption("Portlet edit/view");
        portletEdit.setEnabled(false);
        main.addComponent(portletEdit);
        portletMax.setCaption("Maximize/normal portlet");
        portletMax.setEnabled(false);
        main.addComponent(portletMax);

        PortletApplicationContext ctx = (PortletApplicationContext) getContext();

        ctx.addPortletListener(this, new DemoPortletListener());
    }

    private class DemoPortletListener implements PortletListener {

        public void handleActionRequest(ActionRequest request,
                ActionResponse response) {

            getMainWindow().showNotification("Action received");

        }

        public void handleRenderRequest(RenderRequest request,
                RenderResponse response) {
            getMainWindow().showNotification(
                    "Portlet status",
                    "mode: " + request.getPortletMode() + "<br/> state: "
                            + request.getWindowState(),
                    Notification.TYPE_TRAY_NOTIFICATION);

            PortletURL url = response.createActionURL();
            try {
                url
                        .setPortletMode((request.getPortletMode() == PortletMode.VIEW ? PortletMode.EDIT
                                : PortletMode.VIEW));
                portletEdit.setResource(new ExternalResource(url.toString()));
            } catch (Exception e) {
                portletEdit.setEnabled(false);
            }

            url = response.createActionURL();
            try {
                url
                        .setWindowState((request.getWindowState() == WindowState.NORMAL ? WindowState.MAXIMIZED
                                : WindowState.NORMAL));
                portletEdit.setResource(new ExternalResource(url.toString()));
            } catch (Exception e) {
                portletEdit.setEnabled(false);
            }

        }
    }
}
