package com.vaadin.demo.themes;

import java.util.Date;

import com.vaadin.Application;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * This Vaadin application demonstrates all the available styles that the
 * Reindeer theme includes.
 * 
 * @author Jouni Koivuviita, IT Mill Ltd.
 * 
 */
@SuppressWarnings("serial")
public class ReindeerThemeStyles extends Application {

    private Window main;
    private VerticalLayout mainLayout;

    @Override
    public void init() {
        setTheme("reindeer-style-example");

        main = new Window("Vaadin Reindeer Theme - Included Style Names");
        setMainWindow(main);

        mainLayout = (VerticalLayout) main.getContent();
        mainLayout.setSpacing(true);

        mainLayout.addComponent(new H1("Reindeer theme style reference"));
        mainLayout.addComponent(buildBackgroundToggles());
        mainLayout.addComponent(buildLabels());
        mainLayout.addComponent(buildButtons());
        mainLayout.addComponent(buildTextFields());
        mainLayout.addComponent(buildSelects());
        mainLayout.addComponent(buildDateFields());
        mainLayout.addComponent(buildTabSheets());
        mainLayout.addComponent(buildPanels());
        mainLayout.addComponent(buildTables());
        mainLayout.addComponent(buildWindows());
        mainLayout.addComponent(buildSplitPanels());

    }

    private VerticalLayout buildBackgroundToggles() {
        VerticalLayout l = new VerticalLayout();
        l.setSpacing(true);
        l.setMargin(true, false, true, false);
        l.setWidth(null);

        l.addComponent(new Ruler());

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        l.addComponent(hl);

        Button toggle = new Button("Toggle blue style for main layout",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (!mainLayout.getStyleName().contains("blue")) {
                            mainLayout.setStyleName("blue");
                        } else {
                            mainLayout.setStyleName("");
                        }
                    }
                });
        hl.addComponent(toggle);

        toggle = new Button("Toggle black style for main layout",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (!mainLayout.getStyleName().contains("black")) {
                            mainLayout.setStyleName("black");
                        } else {
                            mainLayout.setStyleName("");
                        }
                    }
                });
        hl.addComponent(toggle);

        Label info = new Label(
                "These buttons only change the style name of the main layout in the application. No other changes are needed in order to make the contained components to change style accordingly.");
        info.setStyleName("light");
        l.addComponent(info);

        info = new Label(
                "Note, though, that some components don't different style on a black background, and some styles aren't designed to be used on a black background. Some components aren't even supported to be used on black background currently (i.e. normal Panel).");
        info.setStyleName("light");
        l.addComponent(info);

        l.addComponent(new Ruler());

        return l;
    }

    private VerticalLayout buildLabels() {
        VerticalLayout l = new VerticalLayout();
        l.setSpacing(true);
        l.setMargin(false, false, true, false);
        l.setWidth("400px");

        Label h1 = new H1("Header Label (style \"h1\")");

        Label text = new Label(
                "This is a normal Label. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam lectus. Sed lectus purus, volutpat sit amet, volutpat sed, venenatis ut, libero. Phasellus auctor.");

        Label h2 = new H2("Second Level Header (style \"h2\")");

        Label light = new Label(
                "This is a light (stylename \"light\") Label, that can be used to display secondary information to the user. Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        light.setStyleName("light");

        l.addComponent(h1);
        l.addComponent(text);
        l.addComponent(h2);
        l.addComponent(light);

        return l;
    }

    private VerticalLayout buildButtons() {
        Base l = new Base("Buttons");

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);

        Button b = new Button("Primary style");
        b.setStyleName("primary");
        hl.addComponent(b);

        b = new Button("Normal");
        hl.addComponent(b);

        b = new Button("Disabled");
        b.setEnabled(false);
        hl.addComponent(b);

        b = new Button("Small style");
        b.setStyleName("small");
        hl.addComponent(b);

        b = new Button("Link style");
        b.setStyleName("link");

        l.addComponent(hl);

        return l;
    }

    private VerticalLayout buildTextFields() {
        Base l = new Base("Text Fields");

        l.addComponent(new TextField("Normal Text field"));
        TextField tf = new TextField();
        tf.setStyleName("small");
        tf.setInputPrompt("Small style textfield");
        l.addComponent(tf);

        return l;
    }

    private VerticalLayout buildSelects() {
        Base l = new Base("Selects");

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        l.addComponent(hl);

        AbstractSelect cb = new ComboBox("Combo box");
        AbstractSelect nat = new NativeSelect("Native Select");
        AbstractSelect list = new ListSelect("List Select");
        AbstractSelect twincol = new TwinColSelect("Twin Column Select");

        for (int i = 0; i < 25; i++) {
            cb.addItem("Item " + i);
            nat.addItem("Item " + i);
            list.addItem("Item " + i);
            twincol.addItem("Item " + i);
        }

        hl.addComponent(cb);
        hl.addComponent(nat);
        hl.addComponent(list);
        hl.addComponent(twincol);

        return l;
    }

    private VerticalLayout buildDateFields() {
        Base l = new Base("Date Fields");

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        l.addComponent(hl);

        DateField df = new DateField("Normal DateField");
        df.setValue(new Date());
        df.setResolution(DateField.RESOLUTION_MIN);
        hl.addComponent(df);

        df = new InlineDateField("Inline DateField");
        df.setValue(new Date());
        df.setResolution(DateField.RESOLUTION_DAY);
        hl.addComponent(df);

        df = new InlineDateField("Inline DateField, year resolution");
        df.setValue(new Date());
        df.setResolution(DateField.RESOLUTION_YEAR);
        hl.addComponent(df);

        return l;
    }

    private VerticalLayout buildTabSheets() {
        Base l = new Base("Tabsheets");

        TabSheet ts = new TabSheet();
        ts.setHeight("100px");
        TabSheet ts2 = new TabSheet();
        ts2.setCaption("Bar style");
        ts2.setStyleName("bar");
        TabSheet ts3 = new TabSheet();
        ts3.setStyleName("minimal");
        ts3.setCaption("Minimal style");

        for (int i = 0; i < 10; i++) {
            ts.addTab(new Label(), "Tab Caption" + i, null);
            ts2.addTab(new Label(), "Tab Caption" + i, null);
            ts3.addTab(new Label(), "Tab Caption" + i, null);
        }

        l.addComponent(ts);
        l.addComponent(ts2);
        l.addComponent(ts3);

        return l;
    }

    private VerticalLayout buildPanels() {
        Base l = new Base("Panels");

        Panel p = new Panel("Normal panel");
        p.setHeight("100px");
        p.addComponent(new Label("Panel content"));

        Panel p2 = new Panel("Light style panel");
        p2.setStyleName("light");
        p2.addComponent(new Label("Panel content"));

        l.addComponent(p);
        l.addComponent(p2);

        return l;
    }

    private VerticalLayout buildTables() {
        Base l = new Base("Tables (Grids)");

        for (int i = 0; i < 3; i++) {

            Table t = new Table();
            t.setWidth("100%");
            t.setPageLength(4);
            t.setSelectable(true);
            t.setColumnCollapsingAllowed(true);
            t.setColumnReorderingAllowed(true);

            if (i == 1) {
                t.setStyleName("strong");
                t.setCaption("Strong style");
            } else if (i == 2) {
                t.setStyleName("borderless");
                t.setCaption("Borderless style");
            }

            t.addContainerProperty("First", String.class, null);
            t.addContainerProperty("Second", String.class, null);
            t.addContainerProperty("Third", String.class, null);

            for (int j = 0; j < 100; j++) {
                t.addItem(new Object[] { "Foo " + j, "Bar value " + j,
                        "Last column value " + j }, j);
            }

            l.addComponent(t);
        }
        return l;
    }

    private VerticalLayout buildWindows() {
        Base l = new Base("Sub windows");
        l.setHeight("260px");

        Window w = new Window("Normal window");
        w.setWidth("200px");
        w.setHeight("180px");
        w.setPositionX(20);
        w.setPositionY(2000);
        main.addWindow(w);

        w = new Window("Window, no resize");
        w.setResizable(false);
        w.setWidth("200px");
        w.setHeight("180px");
        w.setPositionX(240);
        w.setPositionY(2000);
        main.addWindow(w);

        w = new Window("Light window");
        w.setWidth("200px");
        w.setHeight("180px");
        w.setStyleName("light");
        w.setPositionX(460);
        w.setPositionY(2000);
        main.addWindow(w);

        w = new Window("Black window");
        w.setWidth("200px");
        w.setHeight("180px");
        w.setStyleName("black");
        w.setPositionX(680);
        w.setPositionY(2000);
        main.addWindow(w);

        return l;
    }

    private VerticalLayout buildSplitPanels() {
        Base l = new Base("Split panels");
        l.setHeight("260px");

        SplitPanel sp = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        sp.setFirstComponent(new Label("Normal SplitPanel"));
        sp.setHeight("200px");

        SplitPanel sp2 = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        sp2.setStyleName("small");
        sp.setSecondComponent(sp2);
        sp2.setSecondComponent(new Label("small style SplitPanel"));

        l.addComponent(sp);
        l.setExpandRatio(sp, 1);

        return l;
    }

    private class H1 extends Label {
        public H1(String caption) {
            super(caption);
            setStyleName("h1");
        }
    }

    private class H2 extends Label {
        public H2(String caption) {
            super(caption);
            setStyleName("h2");
        }
    }

    private class Ruler extends Label {
        public Ruler() {
            super("<hr />", Label.CONTENT_XHTML);
        }
    }

    private class Base extends VerticalLayout {
        public Base(String header) {
            setSpacing(true);
            setMargin(false, false, true, false);
            addComponent(new H2(header));
        }
    }

}
