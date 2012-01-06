package com.vaadin.tests.components.root;

import com.vaadin.RootRequiresMoreInformationException;
import com.vaadin.annotations.EagerInit;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedRequest.BrowserDetails;
import com.vaadin.tests.components.AbstractTestApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.Label.ContentMode;
import com.vaadin.ui.Link;
import com.vaadin.ui.Root;

public class LazyInitRoots extends AbstractTestApplication {

    @EagerInit
    private static class EagerInitRoot extends Root {
        @Override
        public void init(WrappedRequest request) {
            addComponent(getRequestInfo("EagerInitRoot", request));
        }
    }

    @Override
    public Root getRoot(WrappedRequest request)
            throws RootRequiresMoreInformationException {
        if (request.getParameter("lazyCreate") != null) {
            // Root created on second request
            BrowserDetails browserDetails = request.getBrowserDetails();
            if (browserDetails == null
                    || browserDetails.getUriFragment() == null) {
                throw new RootRequiresMoreInformationException();
            } else {
                Root root = new Root() {
                    @Override
                    protected void init(WrappedRequest request) {
                        addComponent(getRequestInfo("LazyCreateRoot", request));
                    }
                };
                return root;
            }
        } else if (request.getParameter("eagerInit") != null) {
            // Root inited on first request
            return new EagerInitRoot();
        } else {
            // The standard root
            Root root = new Root() {
                @Override
                protected void init(WrappedRequest request) {
                    addComponent(getRequestInfo("NormalRoot", request));

                    Link lazyCreateLink = new Link("Open lazyCreate root",
                            new ExternalResource(getURL()
                                    + "?lazyCreate#lazyCreate"));
                    lazyCreateLink.setTargetName("_blank");
                    addComponent(lazyCreateLink);

                    Link lazyInitLink = new Link("Open eagerInit root",
                            new ExternalResource(getURL()
                                    + "?eagerInit#eagerInit"));
                    lazyInitLink.setTargetName("_blank");
                    addComponent(lazyInitLink);
                }
            };

            return root;
        }
    }

    public static Label getRequestInfo(String name, WrappedRequest request) {
        String info = name;
        info += "<br />pathInfo: " + request.getRequestPathInfo();
        info += "<br />parameters: " + request.getParameterMap().keySet();
        info += "<br />uri fragment: "
                + request.getBrowserDetails().getUriFragment();
        return new Label(info, ContentMode.XHTML);
    }

    @Override
    protected String getTestDescription() {
        return "BrowserDetails should be available in Application.getRoot if RootRequiresMoreInformation has been thrown and in Root.init if the root has the @RootInitRequiresBrowserDetals annotation";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7883); // + #7882 + #7884
    }

}
