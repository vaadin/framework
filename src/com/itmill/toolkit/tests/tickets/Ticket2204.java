package com.itmill.toolkit.tests.tickets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.Accordion;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ComponentContainer;
import com.itmill.toolkit.ui.CoordinateLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket2204 extends Application {

    private List<RichTextArea> textAreas = new ArrayList<RichTextArea>();
    private TabSheet ts;
    private Map<ComponentContainer, Component> containerToComponent = new HashMap<ComponentContainer, Component>();
    private RichTextArea rta;
    private List<Class<? extends ComponentContainer>> classes = new ArrayList<Class<? extends ComponentContainer>>();

    public void init() {
        classes.add(OrderedLayout.class);
        classes.add(GridLayout.class);
        classes.add(Accordion.class);
        classes.add(TabSheet.class);
        classes.add(Panel.class);
        classes.add(CoordinateLayout.class);
        classes.add(SplitPanel.class);

        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {
        ts = new TabSheet();
        layout.addComponent(ts);

        for (Class c : classes) {
            ts.addTab(createComponent(c), c.getSimpleName(), null);
        }
        rta = new RichTextArea();
        rta.setVisible(false);
        ts.addTab(rta, "Hidden rta", null);

        Button b = new Button("Show area", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                showHide();
            }
        });

        layout.addComponent(b);

        b = new Button("Show tab", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                showTab();
            }
        });

        layout.addComponent(b);

    }

    protected void showTab() {
        rta.setVisible(!rta.isVisible());

    }

    protected void showHide() {
        Component c = containerToComponent.get(ts.getSelectedTab());
        c.setVisible(!c.isVisible());
    }

    private Component createComponent(Class c) {
        RichTextArea textArea = new RichTextArea();
        textArea.setVisible(false);
        textArea.setCaption("This is the textArea");
        textArea.setWidth("200px");
        textArea.setHeight("100px");
        textAreas.add(textArea);
        ComponentContainer cc = null;

        try {
            cc = (ComponentContainer) c.newInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        // if (c == OrderedLayout.class) {
        // cc = new OrderedLayout();
        // } else
        if (c == Accordion.class) {
            // Label l = new Label("Filler");
            // l.setCaption("Filler label");
            // cc.addComponent(l);
        }
        if (c == CoordinateLayout.class) {
            ((Sizeable) cc).setHeight("100px");
        }

        containerToComponent.put(cc, textArea);
        cc.addComponent(textArea);
        if (c == SplitPanel.class) {
            ((Sizeable) cc).setWidth("300px");
            ((Sizeable) cc).setHeight("300px");
            cc.addComponent(new Label("Label"));
        }
        if (c == Panel.class) {
            Layout layout = ((Panel) cc).getLayout();
            containerToComponent.put(cc, layout);
            layout.setVisible(false);
            textArea.setVisible(true);
            return cc;
        }

        return cc;
    }
}
