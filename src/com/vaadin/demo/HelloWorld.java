package com.vaadin.demo;

import java.util.Date;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.Tab;

@SuppressWarnings("serial")
public class HelloWorld extends com.vaadin.Application {

    /**
     * Init is invoked on application load (when a user accesses the application
     * for the first time).
     */
    @Override
    public void init() {

        final boolean errors = true;

        setTheme("reindeer");
        // Main window is the primary browser window
        final Window main = new Window("Hello window");
        setMainWindow(main);

        Panel p = new Panel("Panel with icon");
        p.setIcon(new ThemeResource("../runo/icons/16/document.png"));

        if (errors) {
            p.setComponentError(new UserError("Panel error"));
        }

        Label h1 = new Label("Header Label");
        // h1.setCaption("Header Label");
        h1.setStyleName("h1");
        // h1.setIcon(new ThemeResource("../runo/icons/32/document-image.png"));

        Label text = new Label(
                "This is a normal Label. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam lectus. Sed lectus purus, volutpat sit amet, volutpat sed, venenatis ut, libero. Phasellus auctor.");

        Label h2 = new Label("Second Level Header");
        h2.setStyleName("h2");

        Label text2 = new Label(
                "This is a light style Label, that can be used to display secondary information to the user. Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        text2.setStyleName("light");

        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        vl.setMargin(false, true, false, false);

        vl.addComponent(h1);
        vl.addComponent(text);

        HorizontalLayout l = new HorizontalLayout();
        Button b = new Button("Send Invitation");
        b.setStyleName("primary");
        if (errors) {
            b.setComponentError(new UserError("Button error"));
        }
        Button c = new Button("Cancel");
        c.setIcon(new ThemeResource("../runo/icons/16/document-pdf.png"));
        l.addComponent(b);
        l.addComponent(c);
        l.setSpacing(true);
        if (errors) {
            c.setComponentError(new UserError("Button error"));
        }

        c = new Button("Disabled");
        c.setEnabled(false);
        l.addComponent(c);
        c.setIcon(new ThemeResource("../runo/icons/16/document-pdf.png"));

        c = new Button("Preferences...");
        c.setStyleName("small");
        l.addComponent(c);
        c.setIcon(new ThemeResource("../runo/icons/16/document-ppt.png"));
        if (errors) {
            c.setComponentError(new UserError("Small button error"));
        }

        c = new Button("Link to Somewhere");
        c.setStyleName("link");
        l.addComponent(c);
        // c.setEnabled(false);
        c.setIcon(new ThemeResource("../runo/icons/16/document-doc.png"));
        if (errors) {
            c.setComponentError(new UserError("Link button error"));
        }

        vl.addComponent(l);
        vl.addComponent(text2);

        DateField df = new DateField();
        df.setResolution(DateField.RESOLUTION_MIN);
        if (errors) {
            df.setComponentError(new UserError("Dtefieald error"));
        }
        df.setValue(new Date());
        df.setImmediate(true);
        vl.addComponent(df);

        df = new InlineDateField();
        df.setResolution(DateField.RESOLUTION_DAY);
        if (errors) {
            df.setComponentError(new UserError("Dtefieald error"));
        }
        df.setValue(new Date());
        vl.addComponent(df);

        final HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setMargin(true);
        hl.setWidth("100%");

        hl.addComponent(vl);

        VerticalLayout vl2 = new VerticalLayout();
        vl2.setSpacing(true);
        vl2.addComponent(h2);

        vl2.addComponent(new TextField("Text field"));
        HorizontalLayout hl3 = new HorizontalLayout();
        TextField tf = new TextField();
        tf.setStyleName("small");
        tf.setInputPrompt("Small style textfield");
        hl3.addComponent(tf);
        if (errors) {
            tf.setComponentError(new UserError("textfield error"));
        }
        c = new Button("Add person");
        c.setStyleName("small");
        c.setDescription("Click this button to add some person");
        hl3.addComponent(c);
        hl3.setSpacing(true);
        vl2.addComponent(hl3);
        ComboBox cb = new ComboBox("Combo box");
        cb.addItem("Select item 1");
        cb.addItem("Another item for combobox");
        cb.setItemIcon("Select item 1", new ThemeResource(
                "../runo/icons/16/document.png"));
        for (int i = 0; i < 100; i++) {
            cb.addItem("Item " + i);
        }
        if (errors) {
            cb.setComponentError(new UserError("This is an error in Combobox"));
        }
        cb
                .setDescription("Use this select to do absolute nothing. Or was it everything?");
        // cb.setIcon(new ThemeResource("../runo/icons/16/note.png"));
        cb.setInputPrompt("Language");
        cb.setWidth("200px");
        vl2.addComponent(cb);

        hl.addComponent(vl2);

        SplitPanel sp = new SplitPanel();
        sp.setOrientation(SplitPanel.ORIENTATION_HORIZONTAL);
        sp.setSplitPosition(80, SplitPanel.UNITS_PERCENTAGE);
        // sp.setStyleName("small");
        // sp.setLocked(true);
        p.setContent(sp);
        sp.setHeight("500px");

        VerticalLayout rightLayout = new VerticalLayout();
        // rightLayout.setStyleName("black");
        rightLayout.setSizeFull();
        sp.setSecondComponent(rightLayout);

        sp.setFirstComponent(hl);

        TabSheet ts = new TabSheet();
        Label settings = new Label(
                "This is a normal Label. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam lectus.");
        settings.setCaption("Settings");
        settings.setIcon(new ThemeResource("../runo/icons/16/email.png"));
        Tab t = ts.addTab(settings);
        if (errors) {
            t.setComponentError(new UserError("An error in tabsheet."));
        }
        t.setDescription("testing");
        ts.addTab(new Label("Another label here."), "Date", new ThemeResource(
                "../runo/icons/16/calendar.png"));
        ts.addTab(new Label("Another label here."), "Location", null);
        ts.addTab(new Label("Another label here."), "Author", null);

        vl2.addComponent(ts);
        // ts.setStyleName("minimal");

        main.addComponent(p);
        Button toggleBlue = new Button("Toggle blue style",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (!hl.getStyleName().contains("blue")) {
                            hl.setStyleName("blue");
                        } else {
                            hl.setStyleName("");
                        }

                    }
                });
        main.addComponent(toggleBlue);
        MenuBar mb = new MenuBar();
        final MenuBar.MenuItem i = mb.addItem("File", null);
        mb.setSubmenuIcon(new ThemeResource("menubar/img/submenu-icon.png"));
        i.addItem("New...", null);
        i.addItem("Open...", null);
        i.addItem("Save", new ThemeResource("../runo/icons/16/document.png"),
                null);
        i.addItem("Save As...", null);
        MenuBar.MenuItem i2 = i.addItem("Export", null);
        i2.addItem("Export as CSV", null);
        i2.addItem("Export as PDF", null);
        mb.addItem("Edit", null);
        mb.addItem("View", null);
        mb.addItem("Help", null);
        Button toggleBlack = new Button("Toggle black style",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (!hl.getStyleName().contains("black")) {
                            hl.setStyleName("black");
                        } else {
                            hl.setStyleName("");
                        }
                        i.addItem("new item", null);
                    }
                });
        main.addComponent(toggleBlack);

        final TwinColSelect ls = new TwinColSelect(
                "Test for list selects new item");
        ls.addItem("Dublin");
        ls.addItem("Madrid");
        ls.addItem("Helsinki");
        ls.setNewItemHandler(new NewItemHandler() {
            public void addNewItem(String newItemCaption) {
                ls.addItem(newItemCaption);
            }
        });
        ls.setNewItemsAllowed(true);
        vl2.addComponent(ls);
        ls.setColumns(10);

        Window black = new Window("Settings and Preferences");
        main.addWindow(black);

        black.addComponent(mb);
        mb.setWidth("100%");
        black
                .addComponent(new Label(
                        "This is a normal Label. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam lectus. Sed lectus purus, volutpat sit amet, volutpat sed, venenatis ut, libero. Phasellus auctor."));
        black.setPositionX(400);
        black.setPositionY(80);
        // black.setComponentError(new UserError("Window error"));
        // black.setStyleName("light");
        black.setStyleName("black");
        black.addComponent(new ComboBox("Combo box"));
        black.addComponent(new TextField("Another text field"));
        black.addComponent(new InlineDateField());
        ((VerticalLayout) black.getContent()).setSpacing(true);
        // ((VerticalLayout) black.getContent()).setMargin(false);
        black.getContent().setSizeUndefined();
        // black.setModal(true);
        black.setResizable(false);
        // black.setWidth("500px");
        ProgressIndicator pi = new ProgressIndicator();
        pi.setValue(0.6);
        black.addComponent(pi);
        // pi.setEnabled(false);
        pi = new ProgressIndicator();
        pi.setIndeterminate(true);
        black.addComponent(pi);
        // pi.setEnabled(false);

        CheckBox check = new CheckBox("My checkbox");
        check.setIcon(new ThemeResource("../runo/icons/16/document.png"));
        if (errors) {
            check.setComponentError(new UserError("Checkbox error"));
        }
        main.addComponent(check);
    }
}
