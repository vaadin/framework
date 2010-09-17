/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
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
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.VOverlay;

public final class VDebugConsole extends VOverlay implements Console {

    Element caption = DOM.createDiv();

    private Panel panel;

    private Button clear = new Button("Clear console");
    private Button restart = new Button("Restart app");
    private Button forceLayout = new Button("Force layout");
    private Button analyzeLayout = new Button("Analyze layouts");
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

    private static final String help = "Drag=move, shift-drag=resize, doubleclick=min/max."
            + "Use debug=quiet to log only to browser console.";

    public VDebugConsole() {
        super(false, false);
    }

    private EventPreview dragpreview = new EventPreview() {

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
                    setPixelSize(220, 300);
                } else {
                    panel.setVisible(false);
                    setPixelSize(120, 20);
                }
                collapsed = !collapsed;
            }
            break;
        default:
            break;
        }

    }

    private void minimize() {
        setPixelSize(400, 150);
        setPopupPosition(Window.getClientWidth() - 410,
                Window.getClientHeight() - 160);
    }

    @Override
    public void setPixelSize(int width, int height) {
        panel.setHeight((height - 20) + "px");
        panel.setWidth((width - 2) + "px");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.Console#log(java.lang.String)
     */
    public void log(String msg) {
        if (msg == null) {
            msg = "null";
        }

        logToDebugWindow(msg, false);
        GWT.log(msg);
        consoleLog(msg);
    }

    /**
     * Logs the given message to the debug window.
     * 
     * @param msg
     *            The message to log. Must not be null.
     */
    private void logToDebugWindow(String msg, boolean error) {
        if (error) {
            panel.add(createErrorHtml(msg));
        } else {
            panel.add(new HTML(msg));
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
     * @see com.vaadin.terminal.gwt.client.Console#error(java.lang.String)
     */
    public void error(String msg) {
        if (msg == null) {
            msg = "null";
        }

        logToDebugWindow(msg, true);

        GWT.log(msg);
        consoleErr(msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.Console#printObject(java.lang.
     * Object)
     */
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
     * @see com.vaadin.terminal.gwt.client.Console#dirUIDL(com.vaadin
     * .terminal.gwt.client.UIDL)
     */
    public void dirUIDL(UIDL u, ApplicationConfiguration conf) {
        if (panel.isAttached()) {
            panel.add(new VUIDLBrowser(u, conf));
        }
        consoleDir(u);
        // consoleLog(u.getChildrenAsXML());
    }

    private static native void consoleDir(UIDL u)
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

    public void printLayoutProblems(ValueMap meta, ApplicationConnection ac,
            Set<Paintable> zeroHeightComponents,
            Set<Paintable> zeroWidthComponents) {
        JsArray<ValueMap> valueMapArray = meta
                .getJSValueMapArray("invalidLayouts");
        int size = valueMapArray.length();
        panel.add(new HTML("<div>************************</di>"
                + "<h4>Layouts analyzed on server, total top level problems: "
                + size + " </h4>"));
        if (size > 0) {
            Tree tree = new Tree();

            // Position relative does not work here in IE7
            DOM.setStyleAttribute(tree.getElement(), "position", "");

            TreeItem root = new TreeItem("Root problems");
            for (int i = 0; i < size; i++) {
                printLayoutError(valueMapArray.get(i), root, ac);
            }
            panel.add(tree);
            tree.addItem(root);

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
            Set<Paintable> zeroHeightComponents, ApplicationConnection ac) {
        for (final Paintable paintable : zeroHeightComponents) {
            final Container layout = Util.getLayout((Widget) paintable);

            VerticalPanel errorDetails = new VerticalPanel();
            errorDetails.add(new Label("" + Util.getSimpleName(paintable)
                    + " inside " + Util.getSimpleName(layout)));
            final CheckBox emphasisInUi = new CheckBox(
                    "Emphasize components parent in UI (the actual component is not visible)");
            emphasisInUi.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if (paintable != null) {
                        Element element2 = ((Widget) layout).getElement();
                        Widget.setStyleName(element2, "invalidlayout",
                                emphasisInUi.getValue());
                    }
                }
            });
            errorDetails.add(emphasisInUi);
            panel.add(errorDetails);
        }
    }

    private void printLayoutError(ValueMap valueMap, TreeItem parent,
            final ApplicationConnection ac) {
        final String pid = valueMap.getString("id");
        final Paintable paintable = ac.getPaintable(pid);

        TreeItem errorNode = new TreeItem();
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
            public void onClick(ClickEvent event) {
                if (paintable != null) {
                    Element element2 = ((Widget) paintable).getElement();
                    Widget.setStyleName(element2, "invalidlayout",
                            emphasisInUi.getValue());
                }
            }
        });
        errorDetails.add(emphasisInUi);
        errorNode.setWidget(errorDetails);
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
        parent.addItem(errorNode);
    }

    public void log(Throwable e) {
        log(e.getMessage());
        GWT.log(e.getMessage(), e);
    }

    public void error(Throwable e) {
        error(e.getMessage());
        GWT.log(e.getMessage(), e);
    }

    public void init() {
        panel = new FlowPanel();
        if (!quietMode) {
            DOM.appendChild(getContainerElement(), caption);
            setWidget(panel);
            caption.setClassName("v-debug-console-caption");
            setStyleName("v-debug-console");
            DOM.setStyleAttribute(getElement(), "zIndex", 20000 + "");
            DOM.setStyleAttribute(getElement(), "overflow", "hidden");

            sinkEvents(Event.ONDBLCLICK);

            sinkEvents(Event.MOUSEEVENTS);

            panel.setStyleName("v-debug-console-content");

            caption.setInnerHTML("Debug window");
            caption.setTitle(help);

            show();
            minimize();

            actions = new HorizontalPanel();
            actions.add(clear);
            actions.add(restart);
            actions.add(forceLayout);
            actions.add(analyzeLayout);

            panel.add(actions);

            panel.add(new HTML("<i>" + help + "</i>"));

            clear.addClickHandler(new ClickHandler() {
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
                public void onClick(ClickEvent event) {
                    // TODO for each client in appconf force layout
                    // VDebugConsole.this.client.forceLayout();
                }
            });

            analyzeLayout.addClickHandler(new ClickHandler() {
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

        }
        log("Widget set is built on version: "
                + ApplicationConfiguration.VERSION);

        logToDebugWindow("<div class=\"v-theme-version v-theme-version-"
                + ApplicationConfiguration.VERSION.replaceAll("\\.", "_")
                + "\">Warning: widgetset version "
                + ApplicationConfiguration.VERSION
                + " does not seem to match theme version </div>", true);

    }

    public void setQuietMode(boolean quietDebugMode) {
        quietMode = quietDebugMode;
    }
}
