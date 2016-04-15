package com.vaadin.tests.components.ui;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProviderEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUIProvider;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class LazyInitUIs extends AbstractTestUIProvider {

    // @EagerInit
    private static class EagerInitUI extends UI {
        @Override
        public void init(VaadinRequest request) {
            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            setContent(layout);

            layout.addComponent(getRequestInfo("EagerInitUI", request));
        }
    }

    @Override
    public UI createInstance(UICreateEvent event) {
        return getUI(event);
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        return getUI(event).getClass();
    }

    private UI getUI(UIProviderEvent event) {
        VaadinRequest request = event.getRequest();
        if (request.getParameter("lazyCreate") != null) {
            // UI created on second request
            UI uI = new UI() {
                @Override
                protected void init(VaadinRequest request) {
                    VerticalLayout layout = new VerticalLayout();
                    layout.setMargin(true);
                    setContent(layout);

                    layout.addComponent(getRequestInfo("LazyCreateUI", request));
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
                    VerticalLayout layout = new VerticalLayout();
                    layout.setMargin(true);
                    setContent(layout);

                    layout.addComponent(getRequestInfo("NormalUI", request));

                    String location = getPage().getLocation().toString();
                    Link lazyCreateLink = new Link("Open lazyCreate UI",
                            new ExternalResource(location.replaceFirst(
                                    "(\\?|#|$).*", "?lazyCreate#lazyCreate")));
                    lazyCreateLink.setTargetName("_blank");
                    layout.addComponent(lazyCreateLink);

                    Link lazyInitLink = new Link("Open eagerInit UI",
                            new ExternalResource(location.replaceFirst(
                                    "(\\?|#|$).*", "?eagerInit#eagerInit")));
                    lazyInitLink.setTargetName("_blank");
                    layout.addComponent(lazyInitLink);
                }
            };

            return uI;
        }
    }

    public static Label getRequestInfo(String name, VaadinRequest request) {
        String info = name;
        info += "<br />pathInfo: " + request.getPathInfo();
        info += "<br />parameters: " + request.getParameterMap().keySet();
        info += "<br />uri fragment: "
                + Page.getCurrent().getLocation().getFragment();
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
