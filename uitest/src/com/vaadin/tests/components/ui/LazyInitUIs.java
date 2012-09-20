package com.vaadin.tests.components.ui;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUIProvider;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;

public class LazyInitUIs extends AbstractTestUIProvider {

    // @EagerInit
    private static class EagerInitUI extends UI {
        @Override
        public void init(VaadinRequest request) {
            addComponent(getRequestInfo("EagerInitUI", request));
        }
    }

    @Override
    public UI createInstance(VaadinRequest request,
            Class<? extends UI> type) {
        return getUI(request);
    }

    @Override
    public Class<? extends UI> getUIClass(VaadinRequest request) {
        return getUI(request).getClass();
    }

    private UI getUI(VaadinRequest request) {
        if (request.getParameter("lazyCreate") != null) {
            // UI created on second request
            UI uI = new UI() {
                @Override
                protected void init(VaadinRequest request) {
                    addComponent(getRequestInfo("LazyCreateUI", request));
                }
            };
            return uI;
        } else if (request.getParameter("eagerInit") != null) {
            // UI inited on first request
            return new EagerInitUI();
        } else {
            // The standard UI
            UI uI = new UI() {
                @Override
                protected void init(VaadinRequest request) {
                    addComponent(getRequestInfo("NormalUI", request));

                    Link lazyCreateLink = new Link("Open lazyCreate UI",
                            new ExternalResource(VaadinSession.getCurrent()
                                    .getURL() + "?lazyCreate#lazyCreate"));
                    lazyCreateLink.setTargetName("_blank");
                    addComponent(lazyCreateLink);

                    Link lazyInitLink = new Link("Open eagerInit UI",
                            new ExternalResource(VaadinSession.getCurrent()
                                    .getURL() + "?eagerInit#eagerInit"));
                    lazyInitLink.setTargetName("_blank");
                    addComponent(lazyInitLink);
                }
            };

            return uI;
        }
    }

    public static Label getRequestInfo(String name, VaadinRequest request) {
        String info = name;
        info += "<br />pathInfo: " + request.getRequestPathInfo();
        info += "<br />parameters: " + request.getParameterMap().keySet();
        info += "<br />uri fragment: "
                + request.getBrowserDetails().getUriFragment();
        return new Label(info, ContentMode.HTML);
    }

    @Override
    protected String getTestDescription() {
        return "BrowserDetails should be available in Application.getUI if UIRequiresMoreInformation has been thrown and in UI.init if the UI has the @UIInitRequiresBrowserDetals annotation";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7883); // + #7882 + #7884
    }

}
