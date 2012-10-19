/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.VLazyExecutor;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.client.ui.notification.VNotification;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.client.ui.window.WindowConnector;
import com.vaadin.shared.Version;

/**
 * A helper console for client side development. The debug console can also be
 * used to resolve layout issues, inspect the communication between browser and
 * the server, start GWT dev mode and restart application.
 * 
 * <p>
 * This implementation is used vaadin is in debug mode (see manual) and
 * developer appends "?debug" query parameter to url. Debug information can also
 * be shown on browsers internal console only, by appending "?debug=quiet" query
 * parameter.
 * <p>
 * This implementation can be overridden with GWT deferred binding.
 * 
 */
public class VDebugConsole extends VOverlay implements Console {

    private final class HighlightModeHandler implements NativePreviewHandler {
        private final Label label;

        private HighlightModeHandler(Label label) {
            this.label = label;
        }

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            if (event.getTypeInt() == Event.ONKEYDOWN
                    && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                highlightModeRegistration.removeHandler();
                VUIDLBrowser.deHiglight();
                return;
            }
            if (event.getTypeInt() == Event.ONMOUSEMOVE) {
                VUIDLBrowser.deHiglight();
                Element eventTarget = Util.getElementFromPoint(event
                        .getNativeEvent().getClientX(), event.getNativeEvent()
                        .getClientY());
                if (getElement().isOrHasChild(eventTarget)) {
                    return;
                }

                for (ApplicationConnection a : ApplicationConfiguration
                        .getRunningApplications()) {
                    ComponentConnector connector = Util.getConnectorForElement(
                            a, a.getRootConnector().getWidget(), eventTarget);
                    if (connector == null) {
                        connector = Util.getConnectorForElement(a,
                                RootPanel.get(), eventTarget);
                    }
                    if (connector != null) {
                        String pid = connector.getConnectorId();
                        VUIDLBrowser.highlight(connector);
                        label.setText("Currently focused  :"
                                + connector.getClass() + " ID:" + pid);
                        event.cancel();
                        event.consume();
                        event.getNativeEvent().stopPropagation();
                        return;
                    }
                }
            }
            if (event.getTypeInt() == Event.ONCLICK) {
                VUIDLBrowser.deHiglight();
                event.cancel();
                event.consume();
                event.getNativeEvent().stopPropagation();
                highlightModeRegistration.removeHandler();
                Element eventTarget = Util.getElementFromPoint(event
                        .getNativeEvent().getClientX(), event.getNativeEvent()
                        .getClientY());
                for (ApplicationConnection a : ApplicationConfiguration
                        .getRunningApplications()) {
                    ComponentConnector paintable = Util.getConnectorForElement(
                            a, a.getRootConnector().getWidget(), eventTarget);
                    if (paintable == null) {
                        paintable = Util.getConnectorForElement(a,
                                RootPanel.get(), eventTarget);
                    }

                    if (paintable != null) {
                        a.highlightConnector(paintable);
                        return;
                    }
                }
            }
            event.cancel();
        }
    }

    private static final String POS_COOKIE_NAME = "VDebugConsolePos";

    private HandlerRegistration highlightModeRegistration;

    Element caption = DOM.createDiv();

    private Panel panel;

    private Button clear = new Button("C");
    private Button restart = new Button("R");
    private Button forceLayout = new Button("FL");
    private Button analyzeLayout = new Button("AL");
    private Button savePosition = new Button("S");
    private Button highlight = new Button("H");
    private Button connectorStats = new Button("CS");
    private CheckBox devMode = new CheckBox("Dev");
    private CheckBox superDevMode = new CheckBox("SDev");
    private CheckBox autoScroll = new CheckBox("Autoscroll ");
    private HorizontalPanel actions;
    private boolean collapsed = false;

    private boolean resizing;
    private int startX;
    private int startY;
    private int initialW;
    private int initialH;

    private boolean moving = false;

    private int origTop;

    private int origLeft;

    private static final String help = "Drag title=move, shift-drag=resize, doubleclick title=min/max."
            + "Use debug=quiet to log only to browser console.";

    private static final int DEFAULT_WIDTH = 650;
    private static final int DEFAULT_HEIGHT = 400;

    public VDebugConsole() {
        super(false, false);
        getElement().getStyle().setOverflow(Overflow.HIDDEN);
        clear.setTitle("Clear console");
        restart.setTitle("Restart app");
        forceLayout.setTitle("Force layout");
        analyzeLayout.setTitle("Analyze layouts");
        savePosition.setTitle("Save pos");
    }

    private EventPreview dragpreview = new EventPreview() {

        @Override
        public boolean onEventPreview(Event event) {
            onBrowserEvent(event);
            return false;
        }
    };

    private boolean quietMode;

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        switch (DOM.eventGetType(event)) {
        case Event.ONMOUSEDOWN:
            if (DOM.eventGetShiftKey(event)) {
                resizing = true;
                DOM.setCapture(getElement());
                startX = DOM.eventGetScreenX(event);
                startY = DOM.eventGetScreenY(event);
                initialW = VDebugConsole.this.getOffsetWidth();
                initialH = VDebugConsole.this.getOffsetHeight();
                DOM.eventCancelBubble(event, true);
                DOM.eventPreventDefault(event);
                DOM.addEventPreview(dragpreview);
            } else if (DOM.eventGetTarget(event) == caption) {
                moving = true;
                startX = DOM.eventGetScreenX(event);
                startY = DOM.eventGetScreenY(event);
                origTop = getAbsoluteTop();
                origLeft = getAbsoluteLeft();
                DOM.eventCancelBubble(event, true);
                DOM.eventPreventDefault(event);
                DOM.addEventPreview(dragpreview);
            }

            break;
        case Event.ONMOUSEMOVE:
            if (resizing) {
                int deltaX = startX - DOM.eventGetScreenX(event);
                int detalY = startY - DOM.eventGetScreenY(event);
                int w = initialW - deltaX;
                if (w < 30) {
                    w = 30;
                }
                int h = initialH - detalY;
                if (h < 40) {
                    h = 40;
                }
                VDebugConsole.this.setPixelSize(w, h);
                DOM.eventCancelBubble(event, true);
                DOM.eventPreventDefault(event);
            } else if (moving) {
                int deltaX = startX - DOM.eventGetScreenX(event);
                int detalY = startY - DOM.eventGetScreenY(event);
                int left = origLeft - deltaX;
                if (left < 0) {
                    left = 0;
                }
                int top = origTop - detalY;
                if (top < 0) {
                    top = 0;
                }
                VDebugConsole.this.setPopupPosition(left, top);
                DOM.eventCancelBubble(event, true);
                DOM.eventPreventDefault(event);
            }
            break;
        case Event.ONLOSECAPTURE:
        case Event.ONMOUSEUP:
            if (resizing) {
                DOM.releaseCapture(getElement());
                resizing = false;
            } else if (moving) {
                DOM.releaseCapture(getElement());
                moving = false;
            }
            DOM.removeEventPreview(dragpreview);
            break;
        case Event.ONDBLCLICK:
            if (DOM.eventGetTarget(event) == caption) {
                if (collapsed) {
                    panel.setVisible(true);
                    setToDefaultSizeAndPos();
                } else {
                    panel.setVisible(false);
                    setPixelSize(120, 20);
                    setPopupPosition(Window.getClientWidth() - 125,
                            Window.getClientHeight() - 25);
                }
                collapsed = !collapsed;
            }
            break;
        default:
            break;
        }

    }

    private void setToDefaultSizeAndPos() {
        String cookie = Cookies.getCookie(POS_COOKIE_NAME);
        int width, height, top, left;
        boolean autoScrollValue = false;
        if (cookie != null) {
            String[] split = cookie.split(",");
            left = Integer.parseInt(split[0]);
            top = Integer.parseInt(split[1]);
            width = Integer.parseInt(split[2]);
            height = Integer.parseInt(split[3]);
            autoScrollValue = Boolean.valueOf(split[4]);
        } else {
            int windowHeight = Window.getClientHeight();
            int windowWidth = Window.getClientWidth();
            width = DEFAULT_WIDTH;
            height = DEFAULT_HEIGHT;

            if (height > windowHeight / 2) {
                height = windowHeight / 2;
            }
            if (width > windowWidth / 2) {
                width = windowWidth / 2;
            }

            top = windowHeight - (height + 10);
            left = windowWidth - (width + 10);
        }
        setPixelSize(width, height);
        setPopupPosition(left, top);
        autoScroll.setValue(autoScrollValue);
    }

    @Override
    public void setPixelSize(int width, int height) {
        if (height < 20) {
            height = 20;
        }
        if (width < 2) {
            width = 2;
        }
        panel.setHeight((height - 20) + "px");
        panel.setWidth((width - 2) + "px");
        getElement().getStyle().setWidth(width, Unit.PX);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.Console#log(java.lang.String)
     */
    @Override
    public void log(String msg) {
        if (msg == null) {
            msg = "null";
        }
        msg = addTimestamp(msg);
        // remoteLog(msg);

        logToDebugWindow(msg, false);
        GWT.log(msg);
        consoleLog(msg);
        System.out.println(msg);
    }

    private List<String> msgQueue = new LinkedList<String>();

    private ScheduledCommand doSend = new ScheduledCommand() {
        @Override
        public void execute() {
            if (!msgQueue.isEmpty()) {
                RequestBuilder requestBuilder = new RequestBuilder(
                        RequestBuilder.POST, getRemoteLogUrl());
                try {
                    String requestData = "";
                    for (String str : msgQueue) {
                        requestData += str;
                        requestData += "\n";
                    }
                    requestBuilder.sendRequest(requestData,
                            new RequestCallback() {

                                @Override
                                public void onResponseReceived(Request request,
                                        Response response) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onError(Request request,
                                        Throwable exception) {
                                    // TODO Auto-generated method stub

                                }
                            });
                } catch (RequestException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                msgQueue.clear();
            }
        }

    };
    private VLazyExecutor sendToRemoteLog = new VLazyExecutor(350, doSend);

    protected String getRemoteLogUrl() {
        return "http://sun-vehje.local:8080/remotelog/";
    }

    protected void remoteLog(String msg) {
        msgQueue.add(msg);
        sendToRemoteLog.trigger();
    }

    /**
     * Logs the given message to the debug window.
     * 
     * @param msg
     *            The message to log. Must not be null.
     */
    private void logToDebugWindow(String msg, boolean error) {
        Widget row;
        if (error) {
            row = createErrorHtml(msg);
        } else {
            row = new HTML(msg);
        }
        panel.add(row);
        if (autoScroll.getValue()) {
            row.getElement().scrollIntoView();
        }
    }

    private HTML createErrorHtml(String msg) {
        HTML html = new HTML(msg);
        html.getElement().getStyle().setColor("#f00");
        html.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        return html;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.Console#error(java.lang.String)
     */
    @Override
    public void error(String msg) {
        if (msg == null) {
            msg = "null";
        }
        msg = addTimestamp(msg);
        logToDebugWindow(msg, true);

        GWT.log(msg);
        consoleErr(msg);
        System.out.println(msg);

    }

    DateTimeFormat timestampFormat = DateTimeFormat.getFormat("HH:mm:ss:SSS");

    @SuppressWarnings("deprecation")
    private String addTimestamp(String msg) {
        Date date = new Date();
        String timestamp = timestampFormat.format(date);
        return timestamp + " " + msg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.Console#printObject(java.lang. Object)
     */
    @Override
    public void printObject(Object msg) {
        String str;
        if (msg == null) {
            str = "null";
        } else {
            str = msg.toString();
        }
        panel.add((new Label(str)));
        consoleLog(str);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.Console#dirUIDL(com.vaadin.client.UIDL)
     */
    @Override
    public void dirUIDL(ValueMap u, ApplicationConnection client) {
        if (panel.isAttached()) {
            VUIDLBrowser vuidlBrowser = new VUIDLBrowser(u, client);
            vuidlBrowser.setText("Response:");
            panel.add(vuidlBrowser);
        }
        consoleDir(u);
        // consoleLog(u.getChildrenAsXML());
    }

    private static native void consoleDir(ValueMap u)
    /*-{
         if($wnd.console && $wnd.console.log) {
             if($wnd.console.dir) {
                 $wnd.console.dir(u);
             } else {
                 $wnd.console.log(u);
             }
         }

    }-*/;

    private static native void consoleLog(String msg)
    /*-{
         if($wnd.console && $wnd.console.log) {
             $wnd.console.log(msg);
         }
     }-*/;

    private static native void consoleErr(String msg)
    /*-{
         if($wnd.console) {
             if ($wnd.console.error)
                 $wnd.console.error(msg);
             else if ($wnd.console.log)
                 $wnd.console.log(msg);
         }
     }-*/;

    @Override
    public void printLayoutProblems(ValueMap meta, ApplicationConnection ac,
            Set<ComponentConnector> zeroHeightComponents,
            Set<ComponentConnector> zeroWidthComponents) {
        JsArray<ValueMap> valueMapArray = meta
                .getJSValueMapArray("invalidLayouts");
        int size = valueMapArray.length();
        panel.add(new HTML("<div>************************</di>"
                + "<h4>Layouts analyzed on server, total top level problems: "
                + size + " </h4>"));
        if (size > 0) {
            SimpleTree root = new SimpleTree("Root problems");

            for (int i = 0; i < size; i++) {
                printLayoutError(valueMapArray.get(i), root, ac);
            }
            panel.add(root);

        }
        if (zeroHeightComponents.size() > 0 || zeroWidthComponents.size() > 0) {
            panel.add(new HTML("<h4> Client side notifications</h4>"
                    + " <em>The following relative sized components were "
                    + "rendered to a zero size container on the client side."
                    + " Note that these are not necessarily invalid "
                    + "states, but reported here as they might be.</em>"));
            if (zeroHeightComponents.size() > 0) {
                panel.add(new HTML(
                        "<p><strong>Vertically zero size:</strong><p>"));
                printClientSideDetectedIssues(zeroHeightComponents, ac);
            }
            if (zeroWidthComponents.size() > 0) {
                panel.add(new HTML(
                        "<p><strong>Horizontally zero size:</strong><p>"));
                printClientSideDetectedIssues(zeroWidthComponents, ac);
            }
        }
        log("************************");
    }

    private void printClientSideDetectedIssues(
            Set<ComponentConnector> zeroHeightComponents,
            ApplicationConnection ac) {
        for (final ComponentConnector paintable : zeroHeightComponents) {
            final ServerConnector parent = paintable.getParent();

            VerticalPanel errorDetails = new VerticalPanel();
            errorDetails.add(new Label("" + Util.getSimpleName(paintable)
                    + " inside " + Util.getSimpleName(parent)));
            if (parent instanceof ComponentConnector) {
                ComponentConnector parentComponent = (ComponentConnector) parent;
                final Widget layout = parentComponent.getWidget();

                final CheckBox emphasisInUi = new CheckBox(
                        "Emphasize components parent in UI (the actual component is not visible)");
                emphasisInUi.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Element element2 = layout.getElement();
                        Widget.setStyleName(element2, "invalidlayout",
                                emphasisInUi.getValue().booleanValue());
                    }
                });

                errorDetails.add(emphasisInUi);
            }
            panel.add(errorDetails);
        }
    }

    private void printLayoutError(ValueMap valueMap, SimpleTree root,
            final ApplicationConnection ac) {
        final String pid = valueMap.getString("id");
        final ComponentConnector paintable = (ComponentConnector) ConnectorMap
                .get(ac).getConnector(pid);

        SimpleTree errorNode = new SimpleTree();
        VerticalPanel errorDetails = new VerticalPanel();
        errorDetails.add(new Label(Util.getSimpleName(paintable) + " id: "
                + pid));
        if (valueMap.containsKey("heightMsg")) {
            errorDetails.add(new Label("Height problem: "
                    + valueMap.getString("heightMsg")));
        }
        if (valueMap.containsKey("widthMsg")) {
            errorDetails.add(new Label("Width problem: "
                    + valueMap.getString("widthMsg")));
        }
        final CheckBox emphasisInUi = new CheckBox("Emphasize component in UI");
        emphasisInUi.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (paintable != null) {
                    Element element2 = paintable.getWidget().getElement();
                    Widget.setStyleName(element2, "invalidlayout",
                            emphasisInUi.getValue());
                }
            }
        });
        errorDetails.add(emphasisInUi);
        errorNode.add(errorDetails);
        if (valueMap.containsKey("subErrors")) {
            HTML l = new HTML(
                    "<em>Expand this node to show problems that may be dependent on this problem.</em>");
            errorDetails.add(l);
            JsArray<ValueMap> suberrors = valueMap
                    .getJSValueMapArray("subErrors");
            for (int i = 0; i < suberrors.length(); i++) {
                ValueMap value = suberrors.get(i);
                printLayoutError(value, errorNode, ac);
            }

        }
        root.add(errorNode);
    }

    @Override
    public void log(Throwable e) {
        if (e instanceof UmbrellaException) {
            UmbrellaException ue = (UmbrellaException) e;
            for (Throwable t : ue.getCauses()) {
                log(t);
            }
            return;
        }
        log(Util.getSimpleName(e) + ": " + e.getMessage());
        GWT.log(e.getMessage(), e);
    }

    @Override
    public void error(Throwable e) {
        handleError(e, this);
    }

    static void handleError(Throwable e, Console target) {
        if (e instanceof UmbrellaException) {
            UmbrellaException ue = (UmbrellaException) e;
            for (Throwable t : ue.getCauses()) {
                target.error(t);
            }
            return;
        }
        String exceptionText = Util.getSimpleName(e);
        String message = e.getMessage();
        if (message != null && message.length() != 0) {
            exceptionText += ": " + e.getMessage();
        }
        target.error(exceptionText);
        GWT.log(e.getMessage(), e);
        if (!GWT.isProdMode()) {
            e.printStackTrace();
        }
        try {
            VNotification.createNotification(VNotification.DELAY_FOREVER).show(
                    "<h1>Uncaught client side exception</h1><br />"
                            + exceptionText, VNotification.CENTERED, "error");
        } catch (Exception e2) {
            // Just swallow this exception
        }
    }

    @Override
    public void init() {
        panel = new FlowPanel();
        if (!quietMode) {
            DOM.appendChild(getContainerElement(), caption);
            setWidget(panel);
            caption.setClassName("v-debug-console-caption");
            setStyleName("v-debug-console");
            getElement().getStyle().setZIndex(20000);
            getElement().getStyle().setOverflow(Overflow.HIDDEN);

            sinkEvents(Event.ONDBLCLICK);

            sinkEvents(Event.MOUSEEVENTS);

            panel.setStyleName("v-debug-console-content");

            caption.setInnerHTML("Debug window");
            caption.getStyle().setHeight(25, Unit.PX);
            caption.setTitle(help);

            show();
            setToDefaultSizeAndPos();

            actions = new HorizontalPanel();
            Style style = actions.getElement().getStyle();
            style.setPosition(Position.ABSOLUTE);
            style.setBackgroundColor("#666");
            style.setLeft(135, Unit.PX);
            style.setHeight(25, Unit.PX);
            style.setTop(0, Unit.PX);

            actions.add(clear);
            actions.add(restart);
            actions.add(forceLayout);
            actions.add(analyzeLayout);
            actions.add(highlight);
            actions.add(connectorStats);
            connectorStats.setTitle("Show connector statistics for client");
            highlight
                    .setTitle("Select a component and print details about it to the server log and client side console.");
            actions.add(savePosition);
            savePosition
                    .setTitle("Saves the position and size of debug console to a cookie");
            actions.add(autoScroll);
            addDevMode();
            addSuperDevMode();

            autoScroll
                    .setTitle("Automatically scroll so that new messages are visible");

            panel.add(actions);

            panel.add(new HTML("<i>" + help + "</i>"));

            clear.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    int width = panel.getOffsetWidth();
                    int height = panel.getOffsetHeight();
                    panel = new FlowPanel();
                    panel.setPixelSize(width, height);
                    panel.setStyleName("v-debug-console-content");
                    panel.add(actions);
                    setWidget(panel);
                }
            });

            restart.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {

                    String queryString = Window.Location.getQueryString();
                    if (queryString != null
                            && queryString.contains("restartApplications")) {
                        Window.Location.reload();
                    } else {
                        String url = Location.getHref();
                        String separator = "?";
                        if (url.contains("?")) {
                            separator = "&";
                        }
                        if (!url.contains("restartApplication")) {
                            url += separator;
                            url += "restartApplication";
                        }
                        if (!"".equals(Location.getHash())) {
                            String hash = Location.getHash();
                            url = url.replace(hash, "") + hash;
                        }
                        Window.Location.replace(url);
                    }

                }
            });

            forceLayout.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    for (ApplicationConnection applicationConnection : ApplicationConfiguration
                            .getRunningApplications()) {
                        applicationConnection.forceLayout();
                    }
                }
            });

            analyzeLayout.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    List<ApplicationConnection> runningApplications = ApplicationConfiguration
                            .getRunningApplications();
                    for (ApplicationConnection applicationConnection : runningApplications) {
                        applicationConnection.analyzeLayouts();
                    }
                }
            });
            analyzeLayout
                    .setTitle("Analyzes currently rendered view and "
                            + "reports possible common problems in usage of relative sizes."
                            + "Will cause server visit/rendering of whole screen and loss of"
                            + " all non committed variables form client side.");

            savePosition.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    String pos = getAbsoluteLeft() + "," + getAbsoluteTop()
                            + "," + getOffsetWidth() + "," + getOffsetHeight()
                            + "," + autoScroll.getValue();
                    Cookies.setCookie(POS_COOKIE_NAME, pos);
                }
            });

            highlight.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    final Label label = new Label("--");
                    log("<i>Use mouse to select a component or click ESC to exit highlight mode.</i>");
                    panel.add(label);
                    highlightModeRegistration = Event
                            .addNativePreviewHandler(new HighlightModeHandler(
                                    label));

                }
            });

        }
        connectorStats.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (ApplicationConnection a : ApplicationConfiguration
                        .getRunningApplications()) {
                    dumpConnectorInfo(a);
                }
            }
        });
        log("Starting Vaadin client side engine. Widgetset: "
                + GWT.getModuleName());

        log("Widget set is built on version: " + Version.getFullVersion());

        logToDebugWindow("<div class=\"v-theme-version v-theme-version-"
                + Version.getFullVersion().replaceAll("\\.", "_")
                + "\">Warning: widgetset version " + Version.getFullVersion()
                + " does not seem to match theme version </div>", true);

    }

    private void addSuperDevMode() {
        final Storage sessionStorage = Storage.getSessionStorageIfSupported();
        if (sessionStorage == null) {
            return;
        }
        actions.add(superDevMode);
        if (Location.getParameter("superdevmode") != null) {
            superDevMode.setValue(true);
        }
        superDevMode.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                SuperDevMode.redirect(event.getValue());
            }

        });

    }

    private void addDevMode() {
        actions.add(devMode);
        if (Location.getParameter("gwt.codesvr") != null) {
            devMode.setValue(true);
        }
        devMode.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (devMode.getValue()) {
                    addHMParameter();
                } else {
                    removeHMParameter();
                }
            }

            private void addHMParameter() {
                UrlBuilder createUrlBuilder = Location.createUrlBuilder();
                createUrlBuilder.setParameter("gwt.codesvr", "localhost:9997");
                Location.assign(createUrlBuilder.buildString());
            }

            private void removeHMParameter() {
                UrlBuilder createUrlBuilder = Location.createUrlBuilder();
                createUrlBuilder.removeParameter("gwt.codesvr");
                Location.assign(createUrlBuilder.buildString());

            }
        });
    }

    protected void dumpConnectorInfo(ApplicationConnection a) {
        UIConnector root = a.getRootConnector();
        log("================");
        log("Connector hierarchy for Root: " + root.getState().caption + " ("
                + root.getConnectorId() + ")");
        Set<ServerConnector> connectorsInHierarchy = new HashSet<ServerConnector>();
        SimpleTree rootHierachy = dumpConnectorHierarchy(root, "",
                connectorsInHierarchy);
        if (panel.isAttached()) {
            rootHierachy.open(true);
            panel.add(rootHierachy);
        }

        ConnectorMap connectorMap = a.getConnectorMap();
        Collection<? extends ServerConnector> registeredConnectors = connectorMap
                .getConnectors();
        log("Sub windows:");
        Set<ServerConnector> subWindowHierarchyConnectors = new HashSet<ServerConnector>();
        for (WindowConnector wc : root.getSubWindows()) {
            SimpleTree windowHierachy = dumpConnectorHierarchy(wc, "",
                    subWindowHierarchyConnectors);
            if (panel.isAttached()) {
                windowHierachy.open(true);
                panel.add(windowHierachy);
            }
        }
        log("Registered connectors not in hierarchy (should be empty):");
        for (ServerConnector registeredConnector : registeredConnectors) {

            if (connectorsInHierarchy.contains(registeredConnector)) {
                continue;
            }

            if (subWindowHierarchyConnectors.contains(registeredConnector)) {
                continue;
            }
            error(getConnectorString(registeredConnector));

        }
        log("Unregistered connectors in hierarchy (should be empty):");
        for (ServerConnector hierarchyConnector : connectorsInHierarchy) {
            if (!connectorMap.hasConnector(hierarchyConnector.getConnectorId())) {
                error(getConnectorString(hierarchyConnector));
            }

        }

        log("================");

    }

    private SimpleTree dumpConnectorHierarchy(final ServerConnector connector,
            String indent, Set<ServerConnector> connectors) {
        SimpleTree simpleTree = new SimpleTree(getConnectorString(connector)) {
            @Override
            protected void select(ClickEvent event) {
                super.select(event);
                if (connector instanceof ComponentConnector) {
                    VUIDLBrowser.highlight((ComponentConnector) connector);
                }
            }
        };
        simpleTree.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                VUIDLBrowser.deHiglight();
            }
        }, MouseOutEvent.getType());
        connectors.add(connector);

        String msg = indent + "* " + getConnectorString(connector);
        GWT.log(msg);
        consoleLog(msg);
        System.out.println(msg);

        for (ServerConnector c : connector.getChildren()) {
            simpleTree.add(dumpConnectorHierarchy(c, indent + " ", connectors));
        }
        return simpleTree;
    }

    private static String getConnectorString(ServerConnector connector) {
        return Util.getConnectorString(connector);
    }

    @Override
    public void setQuietMode(boolean quietDebugMode) {
        quietMode = quietDebugMode;
    }
}
