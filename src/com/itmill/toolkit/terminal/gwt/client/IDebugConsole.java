/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import java.util.List;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ui.IToolkitOverlay;

public final class IDebugConsole extends IToolkitOverlay implements Console {

    /**
     * Builds number. For example 0-custom_tag in 5.0.0-custom_tag.
     */
    public static final String VERSION;

    /* Initialize version numbers from string replaced by build-script. */
    static {
        if ("@VERSION@".equals("@" + "VERSION" + "@")) {
            VERSION = "5.9.9-INTERNAL-NONVERSIONED-DEBUG-BUILD";
        } else {
            VERSION = "@VERSION@";
        }
    }

    Element caption = DOM.createDiv();

    private final Panel panel;

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

    private ApplicationConnection client;

    private static final String help = "Drag=move, shift-drag=resize, doubleclick=min/max."
            + "Use debug=quiet to log only to browser console.";

    public IDebugConsole(ApplicationConnection client,
            ApplicationConfiguration cnf, boolean showWindow) {
        super(false, false);

        this.client = client;

        panel = new FlowPanel();
        if (showWindow) {
            DOM.appendChild(getContainerElement(), caption);
            setWidget(panel);
            caption.setClassName("i-debug-console-caption");
            setStyleName("i-debug-console");
            DOM.setStyleAttribute(getElement(), "zIndex", 20000 + "");
            DOM.setStyleAttribute(getElement(), "overflow", "hidden");

            sinkEvents(Event.ONDBLCLICK);

            sinkEvents(Event.MOUSEEVENTS);

            panel.setStyleName("i-debug-console-content");

            caption.setInnerHTML("Debug window");
            caption.setTitle(help);

            setWidget(panel);
            show();
            minimize();

            actions = new HorizontalPanel();
            actions.add(clear);
            actions.add(restart);
            actions.add(forceLayout);
            actions.add(analyzeLayout);

            panel.add(actions);

            panel.add(new HTML("<i>" + help + "</i>"));

            clear.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    panel.clear();
                    panel.add(actions);
                }
            });

            restart.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {

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

            forceLayout.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    IDebugConsole.this.client.forceLayout();
                }
            });

            analyzeLayout.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
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
                            + "Will cause server visit/rendering of whole screen + lose of"
                            + " all non committed variables form client side.");

        }

        log("Toolkit application servlet version: " + cnf.getSerletVersion());
        log("Widget set is built on version: " + VERSION);
        log("Application version: " + cnf.getApplicationVersion());

        if (!cnf.getSerletVersion().equals(VERSION)) {
            error("Warning: your widget set seems to be built with a different "
                    + "version than the one used on server. Unexpected "
                    + "behavior may occur.");
        }
    }

    private EventPreview dragpreview = new EventPreview() {

        public boolean onEventPreview(Event event) {
            onBrowserEvent(event);
            return false;
        }
    };

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
                initialW = IDebugConsole.this.getOffsetWidth();
                initialH = IDebugConsole.this.getOffsetHeight();
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
                IDebugConsole.this.setPixelSize(w, h);
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
                IDebugConsole.this.setPopupPosition(left, top);
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
        setPopupPosition(Window.getClientWidth() - 410, Window
                .getClientHeight() - 160);
    }

    @Override
    public void setPixelSize(int width, int height) {
        panel.setHeight((height - 20) + "px");
        panel.setWidth((width - 2) + "px");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.gwt.client.Console#log(java.lang.String)
     */
    public void log(String msg) {
        panel.add(new HTML(msg));
        System.out.println(msg);
        consoleLog(msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.Console#error(java.lang.String)
     */
    public void error(String msg) {
        panel.add((new HTML(msg)));
        System.err.println(msg);
        consoleErr(msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.Console#printObject(java.lang.
     * Object)
     */
    public void printObject(Object msg) {
        panel.add((new Label(msg.toString())));
        consoleLog(msg.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.Console#dirUIDL(com.itmill.toolkit
     * .terminal.gwt.client.UIDL)
     */
    public void dirUIDL(UIDL u) {
        panel.add(u.print_r());
        consoleLog(u.getChildrenAsXML());
    }

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

    public void printLayoutProblems(JSONArray array, ApplicationConnection ac,
            Set<Paintable> zeroHeightComponents,
            Set<Paintable> zeroWidthComponents) {
        int size = array.size();
        panel.add(new HTML("<div>************************</di>"
                + "<h4>Layouts analyzed on server, total top level problems: "
                + size + " </h4>"));
        if (size > 0) {
            Tree tree = new Tree();
            TreeItem root = new TreeItem("Root problems");
            for (int i = 0; i < size; i++) {
                JSONObject error = array.get(i).isObject();
                printLayoutError(error, root, ac);
            }
            panel.add(tree);
            tree.addItem(root);

        }
        if (zeroHeightComponents.size() > 0 || zeroWidthComponents.size() > 0) {
            panel.add(new HTML("<h4> Client side notifications</h4>"
                    + " <em>Following relative sized components where "
                    + "rendered to zero size container on client side."
                    + " Note that these are not necessary invalid "
                    + "states. Just reported here as they might be.</em>"));
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
                    "Emphasis components parent in UI (actual component is not visible)");
            emphasisInUi.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    if (paintable != null) {
                        Element element2 = ((Widget) layout).getElement();
                        Widget.setStyleName(element2, "invalidlayout",
                                emphasisInUi.isChecked());
                    }
                }
            });
            errorDetails.add(emphasisInUi);
            panel.add(errorDetails);
        }
    }

    private void printLayoutError(JSONObject error, TreeItem parent,
            final ApplicationConnection ac) {
        final String pid = error.get("id").isString().stringValue();
        final Paintable paintable = ac.getPaintable(pid);

        TreeItem errorNode = new TreeItem();
        VerticalPanel errorDetails = new VerticalPanel();
        errorDetails.add(new Label(Util.getSimpleName(paintable) + " id: "
                + pid));
        if (error.containsKey("heightMsg")) {
            errorDetails.add(new Label("Height problem: "
                    + error.get("heightMsg")));
        }
        if (error.containsKey("widthMsg")) {
            errorDetails.add(new Label("Width problem: "
                    + error.get("widthMsg")));
        }
        final CheckBox emphasisInUi = new CheckBox("Emphasis component in UI");
        emphasisInUi.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                if (paintable != null) {
                    Element element2 = ((Widget) paintable).getElement();
                    Widget.setStyleName(element2, "invalidlayout", emphasisInUi
                            .isChecked());
                }
            }
        });
        errorDetails.add(emphasisInUi);
        errorNode.setWidget(errorDetails);
        if (error.containsKey("subErrors")) {
            HTML l = new HTML(
                    "<em>Expand this node to show problems that may be dependent on this problem.</em>");
            errorDetails.add(l);
            JSONArray array = error.get("subErrors").isArray();
            for (int i = 0; i < array.size(); i++) {
                JSONValue value = array.get(i);
                if (value != null && value.isObject() != null) {
                    printLayoutError(value.isObject(), errorNode, ac);
                } else {
                    System.out.print(value);
                }
            }

        }
        parent.addItem(errorNode);
    }
}
